package com.gijun.textrpg.adapter.out.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class UserEntity(
    @Id
    val id: String,
    
    @Column("username")
    val username: String,
    
    @Column("email")
    val email: String,
    
    @Column("password_hash")
    val passwordHash: String,
    
    @Column("roles")
    val roles: String, // JSON 형태로 저장
    
    @Column("is_active")
    val isActive: Boolean = true,
    
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Version
    val version: Long? = null
)
