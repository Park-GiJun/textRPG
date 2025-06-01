package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.application.port.out.CharacterEventPublisher
import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CharacterUpdateService(
    private val characterRepository: CharacterRepository,
    private val characterCache: CharacterCachePort,
    private val eventPublisher: CharacterEventPublisher,
    private val characterQueryService: CharacterQueryService
) {
    private val logger = LoggerFactory.getLogger(CharacterUpdateService::class.java)

    suspend fun updateCharacter(command: UpdateCharacterCommand): Character {
        logger.info("Updating character: ${command.characterId}")

        val character = findCharacterOrThrow(command.characterId)
        validateNameChange(command, character)
        
        val updatedCharacter = applyChanges(character, command)
        val savedCharacter = characterRepository.save(updatedCharacter)

        // 캐시 업데이트
        characterCache.saveCharacter(savedCharacter)
        
        // 이름이 변경된 경우 이벤트 발행
        if (command.name != null && command.name != character.name) {
            // 이벤트 퍼블리셔에 이름 변경 메서드 추가 필요
            // eventPublisher.publishCharacterNameChanged(savedCharacter.id, character.name, command.name)
        }

        logger.info("Character updated successfully: ${savedCharacter.id}")
        return savedCharacter
    }

    suspend fun deleteCharacter(characterId: String) {
        logger.info("Deleting character: $characterId")

        if (!characterQueryService.characterExists(characterId)) {
            throw CharacterNotFoundException("Character not found: $characterId")
        }

        characterRepository.deleteById(characterId)
        
        // 캐시에서 제거
        characterCache.evictCharacter(characterId)
        
        // 삭제 이벤트 발행
        eventPublisher.publishCharacterDeleted(characterId)

        logger.info("Character deleted successfully: $characterId")
    }

    private suspend fun findCharacterOrThrow(characterId: String): Character {
        return characterQueryService.getCharacter(characterId)
            ?: throw CharacterNotFoundException("Character not found: $characterId")
    }

    private suspend fun validateNameChange(command: UpdateCharacterCommand, character: Character) {
        if (command.name != null && command.name != character.name) {
            if (characterRepository.existsByName(command.name)) {
                throw CharacterAlreadyExistsException("Character with name '${command.name}' already exists")
            }
        }
    }

    private fun applyChanges(character: Character, command: UpdateCharacterCommand): Character {
        return character.copy(
            name = command.name ?: character.name,
            updatedAt = LocalDateTime.now()
        )
    }
}
