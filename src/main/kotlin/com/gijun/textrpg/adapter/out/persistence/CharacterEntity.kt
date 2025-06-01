package com.gijun.textrpg.adapter.out.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("characters")
data class CharacterEntity(
    @Id
    val id: String,
    
    @Column("name")
    val name: String,
    
    @Column("level")
    val level: Int,
    
    // Health fields
    @Column("current_health")
    val currentHealth: Int,
    
    @Column("max_health")
    val maxHealth: Int,
    
    // Stats fields
    @Column("strength")
    val strength: Int,
    
    @Column("dexterity")
    val dexterity: Int,
    
    @Column("intelligence")
    val intelligence: Int,
    
    @Column("luck")
    val luck: Int,
    
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Version
    val version: Long? = null
)
