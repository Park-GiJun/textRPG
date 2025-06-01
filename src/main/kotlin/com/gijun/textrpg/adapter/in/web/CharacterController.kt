package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.ManageCharacterUseCase
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.domain.character.Stats
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/characters")
class CharacterController(
    private val manageCharacterUseCase: ManageCharacterUseCase
) {

    @PostMapping
    suspend fun createCharacter(
        @Valid @RequestBody request: CreateCharacterRequest
    ): ResponseEntity<CharacterResponse> {
        val customStats = if (request.strength != null || request.dexterity != null || 
                             request.intelligence != null || request.luck != null) {
            Stats(
                strength = request.strength ?: 10,
                dexterity = request.dexterity ?: 10,
                intelligence = request.intelligence ?: 10,
                luck = request.luck ?: 5
            )
        } else null

        val command = CreateCharacterCommand(
            name = request.name,
            customStats = customStats
        )

        val character = manageCharacterUseCase.createCharacter(command)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CharacterResponse.from(character))
    }

    @GetMapping("/{id}")
    suspend fun getCharacter(
        @PathVariable id: String
    ): ResponseEntity<CharacterResponse> {
        val character = manageCharacterUseCase.getCharacter(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(CharacterResponse.from(character))
    }

    @PutMapping("/{id}")
    suspend fun updateCharacter(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateCharacterRequest
    ): ResponseEntity<CharacterResponse> {
        val command = UpdateCharacterCommand(
            characterId = id,
            name = request.name
        )

        val character = manageCharacterUseCase.updateCharacter(command)
        return ResponseEntity.ok(CharacterResponse.from(character))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteCharacter(@PathVariable id: String) {
        manageCharacterUseCase.deleteCharacter(id)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllCharacters(): Flow<CharacterResponse> {
        return manageCharacterUseCase.getAllCharacters()
            .map { CharacterResponse.from(it) }
    }

    @PostMapping("/{id}/experience")
    suspend fun gainExperience(
        @PathVariable id: String,
        @Valid @RequestBody request: GainExperienceRequest
    ): ResponseEntity<CharacterResponse> {
        val character = manageCharacterUseCase.gainExperience(id, request.amount)
        return ResponseEntity.ok(CharacterResponse.from(character))
    }

    // Server-Sent Events example for real-time updates
    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamCharacters(): Flow<CharacterResponse> {
        return manageCharacterUseCase.getAllCharacters()
            .map { CharacterResponse.from(it) }
    }
}

// Request DTOs
data class CreateCharacterRequest(
    val name: String,
    val strength: Int? = null,
    val dexterity: Int? = null,
    val intelligence: Int? = null,
    val luck: Int? = null
)

data class UpdateCharacterRequest(
    val name: String? = null
)

data class GainExperienceRequest(
    val amount: Long
) {
    init {
        require(amount > 0) { "Experience amount must be positive" }
    }
}

// Response DTOs
data class CharacterResponse(
    val id: String,
    val name: String,
    val level: Int,
    val experience: ExperienceResponse,
    val health: HealthResponse,
    val stats: StatsResponse,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(character: com.gijun.textrpg.domain.character.Character): CharacterResponse {
            return CharacterResponse(
                id = character.id,
                name = character.name,
                level = character.level,
                experience = ExperienceResponse(
                    current = character.experience.current,
                    max = character.experience.max,
                    forNextLevel = character.experience.forNextLevel,
                    percentage = character.experience.percentage()
                ),
                health = HealthResponse(
                    current = character.health.current,
                    max = character.health.max,
                    percentage = character.health.percentage()
                ),
                stats = StatsResponse(
                    strength = character.stats.strength,
                    dexterity = character.stats.dexterity,
                    intelligence = character.stats.intelligence,
                    luck = character.stats.luck,
                    totalPower = character.stats.totalPower()
                ),
                createdAt = character.createdAt.toString(),
                updatedAt = character.updatedAt.toString()
            )
        }
    }
}

data class ExperienceResponse(
    val current: Long,
    val max: Long,
    val forNextLevel: Long,
    val percentage: Float
)

data class HealthResponse(
    val current: Int,
    val max: Int,
    val percentage: Float
)

data class StatsResponse(
    val strength: Int,
    val dexterity: Int,
    val intelligence: Int,
    val luck: Int,
    val totalPower: Int
)
