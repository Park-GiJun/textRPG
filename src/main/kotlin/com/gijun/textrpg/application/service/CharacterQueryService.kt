package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CharacterQueryService(
    private val characterRepository: CharacterRepository,
    private val characterCache: CharacterCachePort
) {
    private val logger = LoggerFactory.getLogger(CharacterQueryService::class.java)

    suspend fun getCharacter(characterId: String): Character? {
        logger.debug("Querying character: $characterId")
        
        // 캐시 먼저 확인
        characterCache.getCharacter(characterId)?.let { cached ->
            logger.debug("Character found in cache: ${cached.name}")
            return cached
        }
        
        // 캐시에 없으면 저장소에서 조회
        return characterRepository.findById(characterId)?.also { character ->
            logger.debug("Character loaded from repository: ${character.name}")
            // 조회한 캐릭터를 캐시에 저장
            characterCache.saveCharacter(character)
        }
    }

    fun getAllCharacters(): Flow<Character> {
        logger.debug("Querying all characters")
        return characterRepository.findAll()
            .onEach { character ->
                // 스트리밍하면서 각 캐릭터를 캐시에 저장
                characterCache.saveCharacter(character)
            }
    }

    suspend fun getCharactersByUser(userId: String): Flow<Character> {
        logger.debug("Querying characters for user: $userId")
        return characterRepository.findByUserId(userId)
            .onEach { character ->
                // 스트리밍하면서 각 캐릭터를 캐시에 저장
                characterCache.saveCharacter(character)
            }
    }

    suspend fun getCharacterByName(name: String): Character? {
        logger.debug("Querying character by name: $name")
        
        // 캐시에서 이름으로 조회 시도 (캐시 어댑터에 별도 메서드 필요)
        return characterRepository.findByName(name)?.also { character ->
            characterCache.saveCharacter(character)
        }
    }

    suspend fun characterExists(characterId: String): Boolean {
        // 캐시에서 먼저 확인
        characterCache.getCharacter(characterId)?.let { return true }
        
        // 캐시에 없으면 저장소 확인
        return characterRepository.existsById(characterId)
    }

    suspend fun getCharacterCount(): Long {
        return characterRepository.count()
    }
}
