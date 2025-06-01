package com.gijun.textrpg.domain.character.enums

enum class StatusType {
    // 디버프
    POISON,
    PARALYSIS,
    SLEEP,
    SILENCE,
    BLIND,

    // 버프
    STRENGTH_UP,    // 힘 증가
    DEFENSE_UP,     // 방어력 증가
    REGENERATION,   // 체력 회복
    LUCK_UP         // 운 증가
}