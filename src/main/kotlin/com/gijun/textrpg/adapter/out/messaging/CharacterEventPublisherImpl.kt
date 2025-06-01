package com.gijun.textrpg.adapter.out.messaging

import com.gijun.textrpg.adapter.out.notification.NotificationMessage
import com.gijun.textrpg.application.port.out.CharacterEventPublisher
import com.gijun.textrpg.domain.character.CharacterEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CharacterEventPublisherImpl(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : CharacterEventPublisher {

    private val logger = LoggerFactory.getLogger(CharacterEventPublisherImpl::class.java)
    
    companion object {
        private const val CHARACTER_EVENTS_TOPIC = "character-events"
        private const val NOTIFICATION_TOPIC = "notifications"
    }

    override suspend fun publishCharacterCreated(characterId: String, name: String, stats: com.gijun.textrpg.domain.character.Stats) {
        val event = CharacterEvent.CharacterCreated(
            characterId = characterId,
            name = name,
            stats = stats
        )
        
        publishEvent(event)
        publishNotification("캐릭터 '$name'이(가) 생성되었습니다!", characterId)
    }

    override suspend fun publishCharacterLeveledUp(characterId: String, oldLevel: Int, newLevel: Int) {
        // 현재는 레벨업이 없지만 미래를 위해 구현
        val notification = "캐릭터가 레벨 '$oldLevel'에서 '$newLevel'로 레벨업했습니다!"
        publishNotification(notification, characterId)
    }

    override suspend fun publishCharacterDeleted(characterId: String) {
        val event = CharacterEvent.CharacterDeleted(characterId = characterId)
        
        publishEvent(event)
        publishNotification("캐릭터가 삭제되었습니다.", characterId)
    }

    suspend fun publishCharacterNameChanged(characterId: String, oldName: String, newName: String) {
        val event = CharacterEvent.CharacterNameChanged(
            characterId = characterId,
            oldName = oldName,
            newName = newName
        )
        
        publishEvent(event)
        publishNotification("캐릭터 이름이 '$oldName'에서 '$newName'로 변경되었습니다!", characterId)
    }

    suspend fun publishCharacterDied(characterId: String) {
        val event = CharacterEvent.CharacterDied(characterId = characterId)
        
        publishEvent(event)
        publishNotification("캐릭터가 사망했습니다! 💀", characterId)
    }

    private suspend fun publishEvent(event: CharacterEvent) {
        try {
            kafkaTemplate.send(CHARACTER_EVENTS_TOPIC, event.characterId, event)
                .get() // 동기적으로 전송 확인
            
            logger.info("Published event: {} for character: {}", 
                event::class.simpleName, event.characterId)
        } catch (e: Exception) {
            logger.error("Failed to publish event: {} for character: {}", 
                event::class.simpleName, event.characterId, e)
        }
    }

    private suspend fun publishNotification(message: String, characterId: String) {
        try {
            val notification = NotificationMessage(
                message = message,
                characterId = characterId,
                timestamp = System.currentTimeMillis(),
                type = "CHARACTER_EVENT"
            )
            
            kafkaTemplate.send(NOTIFICATION_TOPIC, characterId, notification)
                .get()
            
            logger.debug("Published notification for character: {}", characterId)
        } catch (e: Exception) {
            logger.error("Failed to publish notification for character: {}", characterId, e)
        }
    }
}
