package com.gijun.textrpg.domain.character

import java.time.LocalDateTime
import java.util.UUID

// 캐릭터 도메인 Entity
data class Character(
    val id: String = UUID.randomUUID().toString(),
    val userId: String, // 소유자 사용자 ID
    val name: String,
    val level: Int = 1,
    val health: Health,
    val stats: Stats,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Character name cannot be blank" }
        require(name.length <= 50) { "Character name cannot exceed 50 characters" }
        require(level > 0) { "Level must be positive" }
    }

    fun takeDamage(amount: Int): Character {
        val newHealth = health.decrease(amount)
        return copy(health = newHealth, updatedAt = LocalDateTime.now())
    }

    fun heal(amount: Int): Character {
        val newHealth = health.increase(amount)
        return copy(health = newHealth, updatedAt = LocalDateTime.now())
    }

    fun isAlive(): Boolean = !health.isDead()
    
    fun getCharacterClass(): String = stats.getPrimaryAttribute()

    companion object {
        // 캐릭터 생성 팩토리 메서드
        fun create(
            userId: String,
            name: String,
            initialStats: Stats = Stats(
                strength = 10,
                dexterity = 10,
                intelligence = 10,
                luck = 5
            )
        ): Character {
            val maxHealth = calculateMaxHealth(1, initialStats.intelligence)
            
            return Character(
                userId = userId,
                name = name,
                health = Health(current = maxHealth, max = maxHealth),
                stats = initialStats
            )
        }
        
        private fun calculateMaxHealth(level: Int, intelligence: Int): Int {
            // 기본 체력 100 + 레벨당 20 + 지능의 절반
            return 100 + (level * 20) + (intelligence / 2)
        }
    }
}
