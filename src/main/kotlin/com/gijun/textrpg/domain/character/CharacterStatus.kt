package com.gijun.textrpg.domain.character

import com.gijun.textrpg.domain.character.enums.StatusType
import java.time.LocalDateTime

data class CharacterStatus(
    val id: Int,
    val characterId: String,
    val statusType: StatusType,
    val intensity: Int,
    val duration: Int,
    val maxDuration: Int,
    val stackable: Boolean = false,
    val appliedAt: LocalDateTime = LocalDateTime.now()
) {
    fun isExpired(): Boolean = duration <= 0

    fun tick(): CharacterStatus = copy(duration = duration - 1)

    fun getEffectDescription(): String = when(statusType) {
        StatusType.POISON -> "턴마다 ${intensity}의 데미지"
        StatusType.PARALYSIS -> "${intensity}턴 동안 물리 스킬 사용 불가"
        StatusType.SLEEP -> "${intensity}턴 동안 행동 불가"
        StatusType.SILENCE -> "${intensity}턴 동안 바법 스킬 사용 불가"
        StatusType.BLIND -> "${intensity}턴 동안 명중률 감소"
        StatusType.STRENGTH_UP -> "힘 +${intensity}"
        else -> statusType.name
    }
}