package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.adapter.`in`.web.dto.CreateCharacterRequest
import com.gijun.textrpg.adapter.`in`.web.dto.UpdateCharacterRequest
import com.gijun.textrpg.adapter.`in`.web.dto.CharacterResponse
import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.ManageCharacterUseCase
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
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
    private val manageCharacterUseCase: ManageCharacterUseCase,
    private val requestMapper: CharacterRequestMapper,
    private val responseMapper: CharacterResponseMapper
) {

    @PostMapping
    suspend fun createCharacter(
        @Valid @RequestBody request: CreateCharacterRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<CharacterResponse> {
        val userId = extractUserIdFromToken(authHeader)
        val command = requestMapper.toCreateCommand(userId, request)
        val character = manageCharacterUseCase.createCharacter(command)
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseMapper.toResponse(character))
    }

    @GetMapping("/{id}")
    suspend fun getCharacter(
        @PathVariable id: String
    ): ResponseEntity<CharacterResponse> {
        val character = manageCharacterUseCase.getCharacter(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(responseMapper.toResponse(character))
    }

    @PutMapping("/{id}")
    suspend fun updateCharacter(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateCharacterRequest
    ): ResponseEntity<CharacterResponse> {
        val command = requestMapper.toUpdateCommand(id, request)
        val character = manageCharacterUseCase.updateCharacter(command)
        
        return ResponseEntity.ok(responseMapper.toResponse(character))
    }

    @GetMapping("/my")
    suspend fun getMyCharacters(
        @RequestHeader("Authorization") authHeader: String
    ): Flow<CharacterResponse> {
        val userId = extractUserIdFromToken(authHeader)
        return manageCharacterUseCase.getCharactersByUser(userId)
            .map { responseMapper.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteCharacter(
        @PathVariable id: String,
        @RequestHeader("Authorization") authHeader: String
    ) {
        val userId = extractUserIdFromToken(authHeader)
        manageCharacterUseCase.deleteCharacter(id, userId)
    }

    private fun extractUserIdFromToken(authHeader: String): String {
        // 임시로 하드코딩, 추후 JWT 파싱 로직으로 교체
        return "temp-user-id"
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllCharacters(): Flow<CharacterResponse> {
        return manageCharacterUseCase.getAllCharacters()
            .map { responseMapper.toResponse(it) }
    }

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamCharacters(): Flow<CharacterResponse> {
        return manageCharacterUseCase.getAllCharacters()
            .map { responseMapper.toResponse(it) }
    }
}
