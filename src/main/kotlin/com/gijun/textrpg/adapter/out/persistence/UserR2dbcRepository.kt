package com.gijun.textrpg.adapter.out.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserR2dbcRepository : CoroutineCrudRepository<UserEntity, String> {
    suspend fun findByUsername(username: String): UserEntity?
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
}
