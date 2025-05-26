package com.gijun.textrpg.application.port.`in`

import com.gijun.textrpg.domain.character.Character
import kotlinx.coroutines.flow.Flow

// Inbound Port Example - Use Case Interface
interface ManageCharacterUseCase {
    
    suspend fun createCharacter(command: CreateCharacterCommand): Character
    
    suspend fun getCharacter(characterId: String): Character?
    
    suspend fun updateCharacter(command: UpdateCharacterCommand): Character
    
    suspend fun deleteCharacter(characterId: String)
    
    fun getAllCharacters(): Flow<Character>
    
    suspend fun gainExperience(characterId: String, amount: Long): Character
}

// Command objects for use cases
data class CreateCharacterCommand(
    val name: String,
    val strength: Int = 10,
    val dexterity: Int = 10,
    val intelligence: Int = 10,
    val vitality: Int = 10
) {
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(name.length <= 50) { "Name cannot exceed 50 characters" }
    }
}

data class UpdateCharacterCommand(
    val characterId: String,
    val name: String? = null,
    val level: Int? = null
) {
    init {
        require(characterId.isNotBlank()) { "Character ID cannot be blank" }
        name?.let {
            require(it.isNotBlank()) { "Name cannot be blank" }
            require(it.length <= 50) { "Name cannot exceed 50 characters" }
        }
        level?.let {
            require(it > 0) { "Level must be positive" }
        }
    }
}

// Query objects for complex searches
data class CharacterSearchQuery(
    val name: String? = null,
    val minLevel: Int? = null,
    val maxLevel: Int? = null,
    val page: Int = 0,
    val size: Int = 20
) {
    init {
        require(page >= 0) { "Page cannot be negative" }
        require(size > 0) { "Size must be positive" }
        require(size <= 100) { "Size cannot exceed 100" }
    }
}
