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
        logger.info("Creating character with name: ${command.name}")

        validateCharacterName(command.name)
        
        val character = createCharacterDomain(command)
        val savedCharacter = characterRepository.save(character)

        // 캐시에 저장
        characterCache.saveCharacter(savedCharacter)
        
        // 도메인 이벤트 발행
        eventPublisher.publishCharacterCreated(savedCharacter.id, savedCharacter.name)

        logger.info("Character created successfully: ${savedCharacter.id}")
        return savedCharacter
    }

    private suspend fun validateCharacterName(name: String) {
        if (characterRepository.existsByName(name)) {
            throw CharacterAlreadyExistsException("Character with name '$name' already exists")
        }
    }

    private fun createCharacterDomain(command: CreateCharacterCommand): Character {
        return if (command.customStats != null) {
            Character.create(name = command.name, initialStats = command.customStats)
        } else {
            Character.create(name = command.name)
        }
    }
}
