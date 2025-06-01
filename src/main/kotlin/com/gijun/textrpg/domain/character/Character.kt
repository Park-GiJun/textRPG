package com.gijun.textrpg.domain.character

import java.time.LocalDateTime
import java.util.UUID

// 경험치 Value Object
data class Experience(
    val current: Long,
    val max: Long,          // 현재 레벨에서 최대 경험치
    val forNextLevel: Long  // 다음 레벨까지 필요한 경험치
) {
    init {
        require(current >= 0) { "Current experience cannot be negative" }
        require(max > 0) { "Max experience must be positive" }
        require(forNextLevel >= 0) { "Experience for next level cannot be negative" }
    }
    
    fun percentage(): Float = (current.toFloat() / max) * 100
    fun isReadyToLevelUp(): Boolean = current >= max
    
    fun gainExperience(amount: Long): Experience {
        val newCurrent = current + amount
        return if (newCurrent >= max) {
            // 레벨업 처리는 Character에서
            copy(current = newCurrent)
        } else {
            copy(current = newCurrent, forNextLevel = max - newCurrent)
        }
    }
}

// 체력 Value Object
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

// 스탯 Value Object
data class Stats(
    val strength: Int,     // 물리 공격력, 근접 데미지
    val dexterity: Int,    // 명중률, 회피율, 원거리 데미지  
    val intelligence: Int, // 마법 공격력, 마나량
    val luck: Int         // 크리티컬, 아이템 드롭률, 회피율 보정
) {
    init {
        require(strength >= 0) { "Strength cannot be negative" }
        require(dexterity >= 0) { "Dexterity cannot be negative" }
        require(intelligence >= 0) { "Intelligence cannot be negative" }
        require(luck >= 0) { "Luck cannot be negative" }
    }

    fun totalPower(): Int = strength + dexterity + intelligence + luck
}

// 캐릭터 도메인 Entity
data class Character(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val level: Int = 1,
    val experience: Experience,
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

    fun gainExperience(amount: Long): Character {
        val newExperience = experience.gainExperience(amount)
        val newLevel = calculateLevel(newExperience.current)
        
        return if (newLevel > level) {
            // 레벨업 발생
            val levelDiff = newLevel - level
            val newStats = levelUpStats(levelDiff)
            val newMaxHealth = calculateMaxHealth(newLevel, newStats.intelligence)
            val newHealth = health.copy(max = newMaxHealth, current = newMaxHealth) // 레벨업 시 체력 풀회복
            
            copy(
                level = newLevel,
                experience = calculateExperienceForLevel(newLevel, newExperience.current),
                health = newHealth,
                stats = newStats,
                updatedAt = LocalDateTime.now()
            )
        } else {
            copy(
                experience = newExperience,
                updatedAt = LocalDateTime.now()
            )
        }
    }

    fun takeDamage(amount: Int): Character {
        val newHealth = health.decrease(amount)
        return copy(health = newHealth, updatedAt = LocalDateTime.now())
    }

    fun heal(amount: Int): Character {
        val newHealth = health.increase(amount)
        return copy(health = newHealth, updatedAt = LocalDateTime.now())
    }

    private fun calculateLevel(totalExp: Long): Int {
        // 레벨별 필요 경험치: level^2 * 100
        var level = 1
        var requiredExp = 0L
        
        while (requiredExp <= totalExp) {
            level++
            requiredExp += level * level * 100L
        }
        
        return level - 1
    }
    
    private fun calculateExperienceForLevel(level: Int, currentExp: Long): Experience {
        val currentLevelExp = (1 until level).sumOf { it * it * 100L }
        val nextLevelExp = currentLevelExp + (level * level * 100L)
        val expInCurrentLevel = currentExp - currentLevelExp
        val maxExpInLevel = level * level * 100L
        val forNextLevel = maxExpInLevel - expInCurrentLevel
        
        return Experience(
            current = expInCurrentLevel,
            max = maxExpInLevel,
            forNextLevel = forNextLevel
        )
    }
    
    private fun levelUpStats(levelDiff: Int): Stats {
        // 레벨업 시 스탯 자동 증가 (레벨당 각 스탯 +2)
        return Stats(
            strength = stats.strength + (levelDiff * 2),
            dexterity = stats.dexterity + (levelDiff * 2),
            intelligence = stats.intelligence + (levelDiff * 2),
            luck = stats.luck + (levelDiff * 1) // 운은 조금 덜 증가
        )
    }
    
    private fun calculateMaxHealth(level: Int, intelligence: Int): Int {
        // 기본 체력 100 + 레벨당 20 + 지능의 절반
        return 100 + (level * 20) + (intelligence / 2)
    }

    companion object {
        // 캐릭터 생성 팩토리 메서드
        fun create(
            name: String,
            initialStats: Stats = Stats(
                strength = 10,
                dexterity = 10,
                intelligence = 10,
                luck = 5
            )
        ): Character {
            val maxHealth = 100 + (initialStats.intelligence / 2)
            
            return Character(
                name = name,
                experience = Experience(current = 0, max = 100, forNextLevel = 100),
                health = Health(current = maxHealth, max = maxHealth),
                stats = initialStats
            )
        }
    }
}

// 도메인 이벤트
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
