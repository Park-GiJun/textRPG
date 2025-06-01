package com.gijun.textrpg.domain.character

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
