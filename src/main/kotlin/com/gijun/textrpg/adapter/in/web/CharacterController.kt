package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.adapter.`in`.web.dto.CreateCharacterRequest
import com.gijun.textrpg.adapter.`in`.web.dto.UpdateCharacterRequest
import com.gijun.textrpg.adapter.`in`.web.dto.CharacterResponse
import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.ManageCharacterUseCase
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.application.port.out.UserRepository
import com.gijun.textrpg.configuration.JwtUtil
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/characters")
class CharacterController(
    private val manageCharacterUseCase: ManageCharacterUseCase,
    private val requestMapper: CharacterRequestMapper,
    private val responseMapper: CharacterResponseMapper,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {

    @PostMapping
    suspend fun createCharacter(
        @Valid @RequestBody request: CreateCharacterRequest
    ): ResponseEntity<CharacterResponse> {
        val userId = getCurrentUserId()
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
    suspend fun getMyCharacters(): Flow<CharacterResponse> {
        val userId = getCurrentUserId()
        return manageCharacterUseCase.getCharactersByUser(userId)
            .map { responseMapper.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteCharacter(
        @PathVariable id: String
    ) {
        val userId = getCurrentUserId()
        manageCharacterUseCase.deleteCharacter(id, userId)
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

    private suspend fun getCurrentUserId(): String {
        return ReactiveSecurityContextHolder.getContext()
            .cast(org.springframework.security.core.context.SecurityContext::class.java)
            .map { it.authentication?.name }
            .awaitSingle()
            ?.let { username -> 
                userRepository.findByUsername(username)?.id 
                    ?: throw RuntimeException("User not found: $username")
            }
            ?: throw RuntimeException("User not authenticated")
    }
}
