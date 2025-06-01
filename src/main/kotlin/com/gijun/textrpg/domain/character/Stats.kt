package com.gijun.textrpg.domain.character

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
    
    fun isBalanced(): Boolean {
        val values = listOf(strength, dexterity, intelligence, luck)
        val maxDiff = values.maxOrNull()!! - values.minOrNull()!!
        return maxDiff <= 10 // 스탯 간 차이가 10 이하면 균형잡힌 것으로 간주
    }
    
    fun getPrimaryAttribute(): String {
        val maxValue = maxOf(strength, dexterity, intelligence, luck)
        return when (maxValue) {
            strength -> "Warrior"
            dexterity -> "Archer"
            intelligence -> "Mage"
            luck -> "Rogue"
            else -> "Balanced"
        }
    }
}
