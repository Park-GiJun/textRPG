package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.ManageCharacterUseCase
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.domain.character.Character
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CharacterService(
    private val characterCreationService: CharacterCreationService,
    private val characterQueryService: CharacterQueryService,
    private val characterUpdateService: CharacterUpdateService
) : ManageCharacterUseCase {

    override suspend fun createCharacter(command: CreateCharacterCommand): Character {
        return characterCreationService.createCharacter(command)
    }

    override suspend fun getCharacter(characterId: String): Character? {
        return characterQueryService.getCharacter(characterId)
    }

    override suspend fun updateCharacter(command: UpdateCharacterCommand): Character {
        return characterUpdateService.updateCharacter(command)
    }

    override suspend fun getCharactersByUser(userId: String): Flow<Character> {
        return characterQueryService.getCharactersByUser(userId)
    }

    override suspend fun deleteCharacter(characterId: String, userId: String) {
        characterUpdateService.deleteCharacter(characterId, userId)
    }

    override fun getAllCharacters(): Flow<Character> {
        return characterQueryService.getAllCharacters()
    }
}
