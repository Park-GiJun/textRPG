package com.gijun.textrpg.adapter.out.persistence

import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import com.gijun.textrpg.domain.character.Health
import com.gijun.textrpg.domain.character.Stats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CharacterRepositoryImpl(
    private val r2dbcRepository: CharacterR2dbcRepository,
    private val characterMapper: CharacterMapper
) : CharacterRepository {

    override suspend fun save(character: Character): Character {
        val entity = characterMapper.toEntity(character)
        val savedEntity = r2dbcRepository.save(entity)
        return characterMapper.toDomain(savedEntity)
    }

    override suspend fun findById(id: String): Character? {
        return r2dbcRepository.findById(id)?.let { characterMapper.toDomain(it) }
    }

    override suspend fun findByName(name: String): Character? {
        return r2dbcRepository.findByName(name)?.let { characterMapper.toDomain(it) }
    }

    override fun findAll(): Flow<Character> {
        return r2dbcRepository.findAll().map { characterMapper.toDomain(it) }
    }

    override fun findByLevelBetween(minLevel: Int, maxLevel: Int): Flow<Character> {
        return r2dbcRepository.findByLevelBetween(minLevel, maxLevel)
            .map { characterMapper.toDomain(it) }
    }

    override suspend fun deleteById(id: String) {
        r2dbcRepository.deleteById(id)
    }

    override suspend fun existsById(id: String): Boolean {
        return r2dbcRepository.existsById(id)
    }

    override suspend fun count(): Long {
        return r2dbcRepository.count()
    }
}

// R2DBC Repository Interface
interface CharacterR2dbcRepository : CoroutineCrudRepository<CharacterEntity, String> {
    suspend fun findByName(name: String): CharacterEntity?
    fun findByLevelBetween(minLevel: Int, maxLevel: Int): Flow<CharacterEntity>
}

// Entity for R2DBC
@Table("characters")
data class CharacterEntity(
    @Id
    val id: String,
    
    @Column("name")
    val name: String,
    
    @Column("level")
    val level: Int,
    
    @Column("experience")
    val experience: Long,
    
    @Column("current_health")
    val currentHealth: Int,
    
    @Column("max_health")
    val maxHealth: Int,
    
    @Column("strength")
    val strength: Int,
    
    @Column("dexterity")
    val dexterity: Int,
    
    @Column("intelligence")
    val intelligence: Int,
    
    @Column("vitality")
    val vitality: Int,
    
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Version
    val version: Long? = null
)

// Mapper between Domain and Entity
@Component
class CharacterMapper {
    
    fun toDomain(entity: CharacterEntity): Character {
        return Character(
            id = entity.id,
            name = entity.name,
            level = entity.level,
            experience = entity.experience,
            health = Health(
                current = entity.currentHealth,
                max = entity.maxHealth
            ),
            stats = Stats(
                strength = entity.strength,
                dexterity = entity.dexterity,
                intelligence = entity.intelligence,
                vitality = entity.vitality
            ),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: Character): CharacterEntity {
        return CharacterEntity(
            id = domain.id,
            name = domain.name,
            level = domain.level,
            experience = domain.experience,
            currentHealth = domain.health.current,
            maxHealth = domain.health.max,
            strength = domain.stats.strength,
            dexterity = domain.stats.dexterity,
            intelligence = domain.stats.intelligence,
            vitality = domain.stats.vitality,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}