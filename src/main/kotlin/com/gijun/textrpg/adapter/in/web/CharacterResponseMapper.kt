package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.adapter.`in`.web.dto.CharacterResponse
import com.gijun.textrpg.adapter.`in`.web.dto.HealthResponse
import com.gijun.textrpg.adapter.`in`.web.dto.StatsResponse
import com.gijun.textrpg.domain.character.Character
import org.springframework.stereotype.Component

@Component
class CharacterResponseMapper {
    
    fun toResponse(character: Character): CharacterResponse {
        return CharacterResponse(
            id = character.id,
            name = character.name,
            level = character.level,
            health = toHealthResponse(character),
            stats = toStatsResponse(character),
            createdAt = character.createdAt.toString(),
            updatedAt = character.updatedAt.toString()
        )
    }
    
    private fun toHealthResponse(character: Character): HealthResponse {
        return HealthResponse(
            current = character.health.current,
            max = character.health.max,
            percentage = character.health.percentage()
        )
    }
    
    private fun toStatsResponse(character: Character): StatsResponse {
        return StatsResponse(
            strength = character.stats.strength,
            dexterity = character.stats.dexterity,
            intelligence = character.stats.intelligence,
            luck = character.stats.luck,
            totalPower = character.stats.totalPower()
        )
    }
}
