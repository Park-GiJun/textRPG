package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.application.port.out.CharacterEventPublisher
import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CharacterCreationService(
    private val characterRepository: CharacterRepository,
    private val characterCache: CharacterCachePort,
    private val eventPublisher: CharacterEventPublisher
) {
    private val logger = LoggerFactory.getLogger(CharacterCreationService::class.java)

    suspend fun createCharacter(command: CreateCharacterCommand): Character {
        logger.info("Creating character with name: ${command.name} for user: ${command.userId}")

        validateCharacterName(command)
        
        val character = createCharacterDomain(command)
        val savedCharacter = characterRepository.save(character)

        // 캐시에 저장
        characterCache.saveCharacter(savedCharacter)
        
        // 도메인 이벤트 발행
        eventPublisher.publishCharacterCreated(savedCharacter.id, savedCharacter.name, savedCharacter.stats)

        logger.info("Character created successfully: ${savedCharacter.id}")
        return savedCharacter
    }

    private suspend fun validateCharacterName(command: CreateCharacterCommand) {
        if (characterRepository.existsByUserIdAndName(command.userId, command.name)) {
            throw CharacterAlreadyExistsException("Character with name '${command.name}' already exists for this user")
        }
    }

    private fun createCharacterDomain(command: CreateCharacterCommand): Character {
        return if (command.customStats != null) {
            Character.create(userId = command.userId, name = command.name, initialStats = command.customStats)
        } else {
            Character.create(userId = command.userId, name = command.name)
        }
    }
}
