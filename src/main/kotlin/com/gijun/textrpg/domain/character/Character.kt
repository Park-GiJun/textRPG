package com.gijun.textrpg.domain.character

import java.time.LocalDateTime
import java.util.UUID

// Domain Entity Example
data class Character(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val level: Int = 1,
    val experience: Long = 0,
    val health: Health,
    val stats: Stats,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Character name cannot be blank" }
        require(level > 0) { "Level must be positive" }
    }

    fun gainExperience(amount: Long): Character {
        val newExperience = experience + amount
        val newLevel = calculateLevel(newExperience)
        return copy(
            experience = newExperience,
            level = newLevel,
            updatedAt = LocalDateTime.now()
        )
    }

    fun takeDamage(amount: Int): Character {
        val newHealth = health.decrease(amount)
        return copy(health = newHealth, updatedAt = LocalDateTime.now())
    }

    fun heal(amount: Int): Character {
        val newHealth = health.increase(amount)
        return copy(health = newHealth, updatedAt = LocalDateTime.now())
    }

    private fun calculateLevel(exp: Long): Int {
        // Simple level calculation logic
        return (exp / 1000).toInt() + 1
    }
}

// Value Object Examples
data class Health(
    val current: Int,
    val max: Int
) {
    init {
        require(current >= 0) { "Current health cannot be negative" }
        require(max > 0) { "Max health must be positive" }
        require(current <= max) { "Current health cannot exceed max health" }
    }

    fun decrease(amount: Int): Health {
        val newCurrent = (current - amount).coerceAtLeast(0)
        return copy(current = newCurrent)
    }

    fun increase(amount: Int): Health {
        val newCurrent = (current + amount).coerceAtMost(max)
        return copy(current = newCurrent)
    }

    fun isDead(): Boolean = current == 0
    fun isFull(): Boolean = current == max
    fun percentage(): Float = (current.toFloat() / max) * 100
}

data class Stats(
    val strength: Int,
    val dexterity: Int,
    val intelligence: Int,
    val vitality: Int
) {
    init {
        require(strength >= 0) { "Strength cannot be negative" }
        require(dexterity >= 0) { "Dexterity cannot be negative" }
        require(intelligence >= 0) { "Intelligence cannot be negative" }
        require(vitality >= 0) { "Vitality cannot be negative" }
    }

    fun totalPower(): Int = strength + dexterity + intelligence + vitality
}

// Domain Event Example
sealed class CharacterEvent {
    abstract val characterId: String
    abstract val occurredAt: LocalDateTime

    data class CharacterCreated(
        override val characterId: String,
        val name: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()

    data class CharacterLeveledUp(
        override val characterId: String,
        val oldLevel: Int,
        val newLevel: Int,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()

    data class CharacterDied(
        override val characterId: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()
}
