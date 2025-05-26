package com.gijun.textrpg.adapter.out.cache

import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.configuration.RedisOperations
import com.gijun.textrpg.domain.character.Character
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CharacterCacheAdapter(
    private val redisOperations: RedisOperations
) : CharacterCachePort {

    private val logger = LoggerFactory.getLogger(CharacterCacheAdapter::class.java)
    
    companion object {
        private const val CHARACTER_KEY_PREFIX = "character:"
        private val CHARACTER_TTL = Duration.ofHours(1)
    }

    override suspend fun getCharacter(id: String): Character? {
        return try {
            val key = "$CHARACTER_KEY_PREFIX$id"
            redisOperations.get(key, Character::class.java)
        } catch (e: Exception) {
            logger.error("Error getting character from cache: ${e.message}", e)
            null
        }
    }

    override suspend fun saveCharacter(character: Character) {
        try {
            val key = "$CHARACTER_KEY_PREFIX${character.id}"
            redisOperations.set(key, character, CHARACTER_TTL)
            logger.debug("Character cached: ${character.id}")
        } catch (e: Exception) {
            logger.error("Error saving character to cache: ${e.message}", e)
        }
    }

    override suspend fun evictCharacter(id: String) {
        try {
            val key = "$CHARACTER_KEY_PREFIX$id"
            redisOperations.delete(key)
            logger.debug("Character cache evicted: $id")
        } catch (e: Exception) {
            logger.error("Error evicting character from cache: ${e.message}", e)
        }
    }

    override suspend fun evictAll() {
        // In production, use SCAN command instead of KEYS
        logger.warn("Evicting all character cache - this should be used carefully in production")
        // Implementation would require pattern-based deletion
    }
}
