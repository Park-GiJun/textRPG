package com.gijun.textrpg.adapter.out.persistence

import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

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

    override fun findByUserId(userId: String): Flow<Character> {
        return r2dbcRepository.findByUserId(userId)
            .map { characterMapper.toDomain(it) }
    }

    override suspend fun deleteById(id: String) {
        r2dbcRepository.deleteById(id)
    }

    override suspend fun existsById(id: String): Boolean {
        return r2dbcRepository.existsById(id)
    }

    override suspend fun existsByName(name: String): Boolean {
        return r2dbcRepository.existsByName(name)
    }
    
    override suspend fun existsByUserIdAndName(userId: String, name: String): Boolean {
        return r2dbcRepository.existsByUserIdAndName(userId, name)
    }

    override suspend fun count(): Long {
        return r2dbcRepository.count()
    }
}
