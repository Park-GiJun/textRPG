package com.gijun.textrpg.adapter.out.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.textrpg.domain.user.User
import com.gijun.textrpg.domain.user.UserRole
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val objectMapper: ObjectMapper
) {
    
    fun toDomain(entity: UserEntity): User {
        val roles = parseRoles(entity.roles)
        
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            passwordHash = entity.passwordHash,
            roles = roles,
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: User): UserEntity {
        val rolesJson = objectMapper.writeValueAsString(domain.roles.map { it.name })
        
        return UserEntity(
            id = domain.id,
            username = domain.username,
            email = domain.email,
            passwordHash = domain.passwordHash,
            roles = rolesJson,
            isActive = domain.isActive,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    private fun parseRoles(rolesJson: String): Set<UserRole> {
        return try {
            val roleNames: List<String> = objectMapper.readValue(
                rolesJson, 
                object : TypeReference<List<String>>() {}
            )
            roleNames.mapNotNull { 
                try { UserRole.valueOf(it) } catch (e: IllegalArgumentException) { null }
            }.toSet()
        } catch (e: Exception) {
            setOf(UserRole.USER) // 기본값
        }
    }
}
