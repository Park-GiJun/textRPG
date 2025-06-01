package com.gijun.textrpg.adapter.out.persistence

import com.gijun.textrpg.domain.character.Character
import com.gijun.textrpg.domain.character.Health
import com.gijun.textrpg.domain.character.Stats
import org.springframework.stereotype.Component

@Component
class CharacterMapper {
    
    fun toDomain(entity: CharacterEntity): Character {
        return Character(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            level = entity.level,
            health = Health(
                current = entity.currentHealth,
                max = entity.maxHealth
            ),
            stats = Stats(
                strength = entity.strength,
                dexterity = entity.dexterity,
                intelligence = entity.intelligence,
                luck = entity.luck
            ),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: Character): CharacterEntity {
        return CharacterEntity(
            id = domain.id,
            userId = domain.userId,
            name = domain.name,
            level = domain.level,
            currentHealth = domain.health.current,
            maxHealth = domain.health.max,
            strength = domain.stats.strength,
            dexterity = domain.stats.dexterity,
            intelligence = domain.stats.intelligence,
            luck = domain.stats.luck,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
