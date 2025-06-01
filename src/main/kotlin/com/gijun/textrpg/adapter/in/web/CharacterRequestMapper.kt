package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.adapter.`in`.web.dto.CreateCharacterRequest
import com.gijun.textrpg.adapter.`in`.web.dto.UpdateCharacterRequest
import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.`in`.UpdateCharacterCommand
import com.gijun.textrpg.domain.character.Stats
import org.springframework.stereotype.Component

@Component
class CharacterRequestMapper {
    
    fun toCreateCommand(request: CreateCharacterRequest): CreateCharacterCommand {
        val customStats = if (hasCustomStats(request)) {
            Stats(
                strength = request.strength ?: 10,
                dexterity = request.dexterity ?: 10,
                intelligence = request.intelligence ?: 10,
                luck = request.luck ?: 5
            )
        } else null

        return CreateCharacterCommand(
            name = request.name,
            customStats = customStats
        )
    }
    
    fun toUpdateCommand(characterId: String, request: UpdateCharacterRequest): UpdateCharacterCommand {
        return UpdateCharacterCommand(
            characterId = characterId,
            name = request.name
        )
    }
    
    private fun hasCustomStats(request: CreateCharacterRequest): Boolean {
        return request.strength != null || 
               request.dexterity != null || 
               request.intelligence != null || 
               request.luck != null
    }
}
