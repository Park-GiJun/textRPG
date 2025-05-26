package com.gijun.textrpg.adapter.out.messaging

import com.gijun.textrpg.application.port.out.CharacterEventPublisher
import kotlinx.coroutines.future.await
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CharacterEventPublisherImpl(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : CharacterEventPublisher {

    private val logger = LoggerFactory.getLogger(CharacterEventPublisherImpl::class.java)
    
    companion object {
        private const val CHARACTER_TOPIC = "character-events"
    }

    override suspend fun publishCharacterCreated(characterId: String, name: String) {
        val event = CharacterCreatedEvent(
            characterId = characterId,
            name = name,
            occurredAt = LocalDateTime.now()
        )
        
        publishEvent(event)
        logger.info("Published character created event: $characterId")
    }

    override suspend fun publishCharacterLeveledUp(characterId: String, oldLevel: Int, newLevel: Int) {
        val event = CharacterLeveledUpEvent(
            characterId = characterId,
            oldLevel = oldLevel,
            newLevel = newLevel,
            occurredAt = LocalDateTime.now()
        )
        
        publishEvent(event)
        logger.info("Published character leveled up event: $characterId ($oldLevel -> $newLevel)")
    }

    override suspend fun publishCharacterDeleted(characterId: String) {
        val event = CharacterDeletedEvent(
            characterId = characterId,
            occurredAt = LocalDateTime.now()
        )
        
        publishEvent(event)
        logger.info("Published character deleted event: $characterId")
    }

    private suspend fun publishEvent(event: Any) {
        try {
            kafkaTemplate.send(CHARACTER_TOPIC, event).await()
            logger.debug("Event published successfully")
        } catch (ex: Exception) {
            logger.error("Failed to publish event", ex)
        }
    }
}

// Event DTOs for Kafka
data class CharacterCreatedEvent(
    val characterId: String,
    val name: String,
    val occurredAt: LocalDateTime
)

data class CharacterLeveledUpEvent(
    val characterId: String,
    val oldLevel: Int,
    val newLevel: Int,
    val occurredAt: LocalDateTime
)

data class CharacterDeletedEvent(
    val characterId: String,
    val occurredAt: LocalDateTime
)