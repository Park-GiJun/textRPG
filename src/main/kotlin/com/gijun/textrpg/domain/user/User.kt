package com.gijun.textrpg.domain.user

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val passwordHash: String,
    val roles: Set<UserRole> = setOf(UserRole.USER),
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(username.length in 3..50) { "Username must be between 3 and 50 characters" }
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }
    }

    fun hasRole(role: UserRole): Boolean = roles.contains(role)
    
    fun isAdmin(): Boolean = hasRole(UserRole.ADMIN)
    
    fun addRole(role: UserRole): User = copy(roles = roles + role)
    
    fun removeRole(role: UserRole): User = copy(roles = roles - role)
    
    fun deactivate(): User = copy(isActive = false, updatedAt = LocalDateTime.now())
    
    fun activate(): User = copy(isActive = true, updatedAt = LocalDateTime.now())
}

enum class UserRole {
    USER, ADMIN, MODERATOR
}
