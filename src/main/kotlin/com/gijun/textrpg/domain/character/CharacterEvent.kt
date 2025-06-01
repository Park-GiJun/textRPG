package com.gijun.textrpg.domain.character

import java.time.LocalDateTime

// 도메인 이벤트
sealed class CharacterEvent {
    abstract val characterId: String
    abstract val occurredAt: LocalDateTime

    data class CharacterCreated(
        override val characterId: String,
        val name: String,
        val stats: Stats,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()

    data class CharacterNameChanged(
        override val characterId: String,
        val oldName: String,
        val newName: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()

    data class CharacterDied(
        override val characterId: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()

    data class CharacterDeleted(
        override val characterId: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : CharacterEvent()
}
