package com.gijun.textrpg.adapter.out.persistence

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CharacterR2dbcRepository : CoroutineCrudRepository<CharacterEntity, String> {
    suspend fun findByName(name: String): CharacterEntity?
    suspend fun existsByName(name: String): Boolean
    fun findByLevelBetween(minLevel: Int, maxLevel: Int): Flow<CharacterEntity>
    fun findByUserId(userId: String): Flow<CharacterEntity>
    suspend fun existsByUserIdAndName(userId: String, name: String): Boolean
}
