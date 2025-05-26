package com.gijun.textrpg.application.port.out

import com.gijun.textrpg.domain.character.Character
import kotlinx.coroutines.flow.Flow

// Outbound Port Example - Repository Interface
interface CharacterRepository {
    
    suspend fun save(character: Character): Character
    
    suspend fun findById(id: String): Character?
    
    suspend fun findByName(name: String): Character?
    
    fun findAll(): Flow<Character>
    
    fun findByLevelBetween(minLevel: Int, maxLevel: Int): Flow<Character>
    
    suspend fun deleteById(id: String)
    
    suspend fun existsById(id: String): Boolean
    
    suspend fun count(): Long
}

// Event Publisher Port
interface CharacterEventPublisher {
    
    suspend fun publishCharacterCreated(characterId: String, name: String)
    
    suspend fun publishCharacterLeveledUp(characterId: String, oldLevel: Int, newLevel: Int)
    
    suspend fun publishCharacterDeleted(characterId: String)
}

// Cache Port
interface CharacterCachePort {
    
    suspend fun getCharacter(id: String): Character?
    
    suspend fun saveCharacter(character: Character)
    
    suspend fun evictCharacter(id: String)
    
    suspend fun evictAll()
}

// External Service Port Example
interface NotificationPort {
    
    suspend fun sendNotification(userId: String, message: String)
    
    suspend fun sendEmail(email: String, subject: String, body: String)
    
    suspend fun sendSlackMessage(channel: String, message: String)
}
