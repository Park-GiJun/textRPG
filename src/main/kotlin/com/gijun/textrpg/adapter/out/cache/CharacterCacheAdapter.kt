package com.gijun.textrpg.adapter.out.cache

import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.domain.character.Character
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CharacterCacheAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, Any>
) : CharacterCachePort {

    private val logger = LoggerFactory.getLogger(CharacterCacheAdapter::class.java)
    
    companion object {
        private const val CHARACTER_KEY_PREFIX = "character:"
        private const val CHARACTER_NAME_KEY_PREFIX = "character:name:"
        private val DEFAULT_TTL = Duration.ofHours(1)
    }

    override suspend fun getCharacter(id: String): Character? {
        return try {
            val key = getCharacterKey(id)
            val cached = redisTemplate.opsForValue()
                .get(key)
                .cast(Character::class.java)
                .block()
            
            if (cached != null) {
                logger.debug("Cache HIT for character: $id")
            } else {
                logger.debug("Cache MISS for character: $id")
            }
            
            cached
        } catch (e: Exception) {
            logger.warn("Error getting character from cache: $id", e)
            null
        }
    }

    override suspend fun saveCharacter(character: Character) {
        try {
            val key = getCharacterKey(character.id)
            val nameKey = getCharacterNameKey(character.name)
            
            // 캐릭터 정보 저장
            redisTemplate.opsForValue()
                .set(key, character, DEFAULT_TTL)
                .block()
            
            // 이름으로 ID 조회를 위한 매핑 저장
            redisTemplate.opsForValue()
                .set(nameKey, character.id, DEFAULT_TTL)
                .block()
            
            logger.debug("Character cached: {} ({})", character.name, character.id)
        } catch (e: Exception) {
            logger.error("Error saving character to cache: ${character.id}", e)
        }
    }

    override suspend fun evictCharacter(id: String) {
        try {
            // 먼저 캐릭터 정보를 가져와서 이름 키도 삭제
            val character = getCharacter(id)
            
            val key = getCharacterKey(id)
            redisTemplate.delete(key).block()
            
            character?.let {
                val nameKey = getCharacterNameKey(it.name)
                redisTemplate.delete(nameKey).block()
            }
            
            logger.debug("Character evicted from cache: $id")
        } catch (e: Exception) {
            logger.error("Error evicting character from cache: $id", e)
        }
    }

    override suspend fun evictAll() {
        try {
            val pattern = "$CHARACTER_KEY_PREFIX*"
            val keys = redisTemplate.keys(pattern).collectList().block() ?: emptyList()
            
            if (keys.isNotEmpty()) {
                redisTemplate.delete(*keys.toTypedArray()).block()
                logger.info("Evicted {} character entries from cache", keys.size)
            }
        } catch (e: Exception) {
            logger.error("Error evicting all characters from cache", e)
        }
    }

    suspend fun getCharacterByName(name: String): Character? {
        return try {
            val nameKey = getCharacterNameKey(name)
            val characterId = redisTemplate.opsForValue()
                .get(nameKey)
                .cast(String::class.java)
                .block()
            
            characterId?.let { getCharacter(it) }
        } catch (e: Exception) {
            logger.warn("Error getting character by name from cache: $name", e)
            null
        }
    }

    suspend fun cacheCharacterList(characters: List<Character>) {
        try {
            characters.forEach { character ->
                saveCharacter(character)
            }
            logger.debug("Cached {} characters", characters.size)
        } catch (e: Exception) {
            logger.error("Error caching character list", e)
        }
    }

    private fun getCharacterKey(id: String): String = "$CHARACTER_KEY_PREFIX$id"
    private fun getCharacterNameKey(name: String): String = "$CHARACTER_NAME_KEY_PREFIX$name"
}
