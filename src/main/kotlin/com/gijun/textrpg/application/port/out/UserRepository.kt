package com.gijun.textrpg.application.port.out

import com.gijun.textrpg.domain.user.User

interface UserRepository {
    
    suspend fun save(user: User): User
    
    suspend fun findById(id: String): User?
    
    suspend fun findByUsername(username: String): User?
    
    suspend fun findByEmail(email: String): User?
    
    suspend fun existsByUsername(username: String): Boolean
    
    suspend fun existsByEmail(email: String): Boolean
    
    suspend fun deleteById(id: String)
    
    suspend fun count(): Long
}
