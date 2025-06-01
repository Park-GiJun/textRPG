package com.gijun.textrpg.application.port.`in`

import com.gijun.textrpg.domain.character.Character
import com.gijun.textrpg.domain.character.Stats
import kotlinx.coroutines.flow.Flow

// Character Management Use Case
interface ManageCharacterUseCase {
    
    suspend fun createCharacter(command: CreateCharacterCommand): Character
    
    suspend fun getCharacter(characterId: String): Character?
    
    suspend fun getCharactersByUser(userId: String): Flow<Character>
    
    suspend fun updateCharacter(command: UpdateCharacterCommand): Character
    
    suspend fun deleteCharacter(characterId: String, userId: String) // 사용자 확인 추가
    
    fun getAllCharacters(): Flow<Character>
}

// Command objects for use cases
data class CreateCharacterCommand(
    val userId: String, // 캐릭터를 생성할 사용자 ID
    val name: String,
    val customStats: Stats? = null // null이면 기본 스탯 사용
) {
    init {
        require(userId.isNotBlank()) { "User ID cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(name.length <= 50) { "Name cannot exceed 50 characters" }
        require(!name.matches(Regex(".*[<>\"'&].*"))) { "Name contains invalid characters" }
    }
}

data class UpdateCharacterCommand(
    val characterId: String,
    val name: String? = null
) {
    init {
        require(characterId.isNotBlank()) { "Character ID cannot be blank" }
        name?.let {
            require(it.isNotBlank()) { "Name cannot be blank" }
            require(it.length <= 50) { "Name cannot exceed 50 characters" }
            require(!it.matches(Regex(".*[<>\"'&].*"))) { "Name contains invalid characters" }
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
