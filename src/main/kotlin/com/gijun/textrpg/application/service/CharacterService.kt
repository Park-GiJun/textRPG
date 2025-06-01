package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.ManageCharacterUseCase
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CharacterService(
    private val characterRepository: CharacterRepository
    // TODO: 나중에 캐시와 이벤트 퍼블리셔 추가
    // private val characterCache: CharacterCachePort,
    // private val eventPublisher: CharacterEventPublisher
) : ManageCharacterUseCase {

    private val logger = LoggerFactory.getLogger(CharacterService::class.java)

    override suspend fun createCharacter(command: CreateCharacterCommand): Character {
        logger.info("Creating character with name: ${command.name}")

        // 이름 중복 체크
        if (characterRepository.existsByName(command.name)) {
            throw CharacterAlreadyExistsException("Character with name '${command.name}' already exists")
        }

        // 도메인 객체 생성 (팩토리 메서드 사용)
        val character = if (command.customStats != null) {
            Character.create(name = command.name, initialStats = command.customStats)
        } else {
            Character.create(name = command.name)
        }

        // 저장
        val savedCharacter = characterRepository.save(character)

        // TODO: 캐시 저장
        // characterCache.saveCharacter(savedCharacter)

        // TODO: 도메인 이벤트 발행
        // eventPublisher.publishCharacterCreated(savedCharacter.id, savedCharacter.name)

        logger.info("Character created successfully: ${savedCharacter.id}")
        return savedCharacter
    }

    override suspend fun getCharacter(characterId: String): Character? {
        // TODO: 캐시 먼저 확인
        // characterCache.getCharacter(characterId)?.let {
        //     logger.debug("Character found in cache: $characterId")
        //     return it
        // }

        // 저장소에서 조회
        return characterRepository.findById(characterId)?.also {
            // TODO: 캐시에 저장
            // characterCache.saveCharacter(it)
            logger.debug("Character loaded from repository: $characterId")
        }
    }

    override suspend fun updateCharacter(command: UpdateCharacterCommand): Character {
        val character = getCharacter(command.characterId)
            ?: throw CharacterNotFoundException("Character not found: ${command.characterId}")

        val updatedCharacter = character.copy(
            name = command.name ?: character.name,
            updatedAt = java.time.LocalDateTime.now()
        )

        // 이름이 변경되는 경우 중복 체크
        if (command.name != null && command.name != character.name) {
            if (characterRepository.existsByName(command.name)) {
                throw CharacterAlreadyExistsException("Character with name '${command.name}' already exists")
            }
        }

        val savedCharacter = characterRepository.save(updatedCharacter)
        
        // TODO: 캐시 업데이트
        // characterCache.saveCharacter(savedCharacter)

        logger.info("Character updated: ${savedCharacter.id}")
        return savedCharacter
    }

    override suspend fun deleteCharacter(characterId: String) {
        if (!characterRepository.existsById(characterId)) {
            throw CharacterNotFoundException("Character not found: $characterId")
        }

        characterRepository.deleteById(characterId)
        
        // TODO: 캐시 제거 및 이벤트 발행
        // characterCache.evictCharacter(characterId)
        // eventPublisher.publishCharacterDeleted(characterId)

        logger.info("Character deleted: $characterId")
    }

    override fun getAllCharacters(): Flow<Character> {
        return characterRepository.findAll()
            // TODO: 스트리밍하면서 각 캐릭터를 캐시에 저장
            // .onEach { character ->
            //     characterCache.saveCharacter(character)
            // }
    }

    override suspend fun gainExperience(characterId: String, amount: Long): Character {
        require(amount > 0) { "Experience amount must be positive" }
        
        val character = getCharacter(characterId)
            ?: throw CharacterNotFoundException("Character not found: $characterId")

        val oldLevel = character.level
        val updatedCharacter = character.gainExperience(amount)

        val savedCharacter = characterRepository.save(updatedCharacter)
        
        // TODO: 캐시 업데이트
        // characterCache.saveCharacter(savedCharacter)

        // TODO: 레벨업 이벤트 발행
        if (savedCharacter.level > oldLevel) {
            // eventPublisher.publishCharacterLeveledUp(
            //     characterId = savedCharacter.id,
            //     oldLevel = oldLevel,
            //     newLevel = savedCharacter.level
            // )
            logger.info("Character leveled up: ${savedCharacter.id} ($oldLevel -> ${savedCharacter.level})")
        }

        logger.debug("Character gained $amount experience: ${savedCharacter.id}")
        return savedCharacter
    }
}

// Custom Exceptions
class CharacterNotFoundException(message: String) : RuntimeException(message)
class CharacterAlreadyExistsException(message: String) : RuntimeException(message)
