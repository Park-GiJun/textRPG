package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.ManageCharacterUseCase
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.application.port.out.CharacterEventPublisher
import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import com.gijun.textrpg.domain.character.Health
import com.gijun.textrpg.domain.character.Stats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val characterCache: CharacterCachePort,
    private val eventPublisher: CharacterEventPublisher
) : ManageCharacterUseCase {

    private val logger = LoggerFactory.getLogger(CharacterService::class.java)

    override suspend fun createCharacter(command: CreateCharacterCommand): Character {
        logger.info("Creating character with name: ${command.name}")

        // Check if name already exists
        characterRepository.findByName(command.name)?.let {
            throw CharacterAlreadyExistsException("Character with name ${command.name} already exists")
        }

        // Create domain object
        val character = Character(
            name = command.name,
            health = Health(current = 100, max = 100),
            stats = Stats(
                strength = command.strength,
                dexterity = command.dexterity,
                intelligence = command.intelligence,
                vitality = command.vitality
            )
        )

        // Save to repository
        val savedCharacter = characterRepository.save(character)

        // Cache the character
        characterCache.saveCharacter(savedCharacter)

        // Publish domain event
        eventPublisher.publishCharacterCreated(savedCharacter.id, savedCharacter.name)

        logger.info("Character created successfully: ${savedCharacter.id}")
        return savedCharacter
    }

    override suspend fun getCharacter(characterId: String): Character? {
        // Try cache first
        characterCache.getCharacter(characterId)?.let {
            logger.debug("Character found in cache: $characterId")
            return it
        }

        // If not in cache, get from repository
        return characterRepository.findById(characterId)?.also {
            // Save to cache for next time
            characterCache.saveCharacter(it)
            logger.debug("Character loaded from repository and cached: $characterId")
        }
    }

    override suspend fun updateCharacter(command: UpdateCharacterCommand): Character {
        val character = getCharacter(command.characterId)
            ?: throw CharacterNotFoundException("Character not found: ${command.characterId}")

        val updatedCharacter = character.copy(
            name = command.name ?: character.name,
            level = command.level ?: character.level
        )

        val savedCharacter = characterRepository.save(updatedCharacter)
        
        // Update cache
        characterCache.saveCharacter(savedCharacter)

        return savedCharacter
    }

    override suspend fun deleteCharacter(characterId: String) {
        if (!characterRepository.existsById(characterId)) {
            throw CharacterNotFoundException("Character not found: $characterId")
        }

        characterRepository.deleteById(characterId)
        characterCache.evictCharacter(characterId)
        eventPublisher.publishCharacterDeleted(characterId)

        logger.info("Character deleted: $characterId")
    }

    override fun getAllCharacters(): Flow<Character> {
        return characterRepository.findAll()
            .onEach { character ->
                // Cache each character as we stream them
                characterCache.saveCharacter(character)
            }
    }

    override suspend fun gainExperience(characterId: String, amount: Long): Character {
        val character = getCharacter(characterId)
            ?: throw CharacterNotFoundException("Character not found: $characterId")

        val oldLevel = character.level
        val updatedCharacter = character.gainExperience(amount)

        val savedCharacter = characterRepository.save(updatedCharacter)
        characterCache.saveCharacter(savedCharacter)

        // Publish level up event if level changed
        if (savedCharacter.level > oldLevel) {
            eventPublisher.publishCharacterLeveledUp(
                characterId = savedCharacter.id,
                oldLevel = oldLevel,
                newLevel = savedCharacter.level
            )
        }

        return savedCharacter
    }
}

// Custom Exceptions
class CharacterNotFoundException(message: String) : RuntimeException(message)
class CharacterAlreadyExistsException(message: String) : RuntimeException(message)
