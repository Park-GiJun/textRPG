package com.gijun.textrpg.adapter.out.notification

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class SlackNotificationAdapter(
    @Value("\${slack.webhook.url:}") private val webhookUrl: String,
    private val webClient: WebClient = WebClient.builder().build()
) {
    
    private val logger = LoggerFactory.getLogger(SlackNotificationAdapter::class.java)
    
    @KafkaListener(topics = ["notifications"], groupId = "slack-notification-group")
    fun handleNotification(notification: NotificationMessage) {
        if (webhookUrl.isBlank()) {
            logger.debug("Slack webhook URL not configured, skipping notification")
            return
        }
        
        sendSlackMessage(notification)
    }
    
    private fun sendSlackMessage(notification: NotificationMessage) {
        val slackMessage = SlackMessage(
            text = "🎮 TextRPG 알림",
            attachments = listOf(
                SlackAttachment(
                    color = getColorByType(notification.type),
                    fields = listOf(
                        SlackField("메시지", notification.message, false),
                        SlackField("캐릭터 ID", notification.characterId, true),
                        SlackField("시간", formatTimestamp(notification.timestamp), true)
                    )
                )
            )
        )
        
        webClient.post()
            .uri(webhookUrl)
            .bodyValue(slackMessage)
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnSuccess { 
                logger.debug("Slack notification sent successfully for character: {}", notification.characterId)
            }
            .doOnError { error ->
                logger.error("Failed to send Slack notification for character: {}", notification.characterId, error)
            }
            .onErrorResume { Mono.empty() }
            .subscribe()
    }
    
    private fun getColorByType(type: String): String = when (type) {
        "CHARACTER_EVENT" -> "good"
        "ERROR" -> "danger"
        "WARNING" -> "warning"
        else -> "#36a64f"
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        return java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}

// Slack 메시지 구조체들
data class SlackMessage(
    val text: String,
    val attachments: List<SlackAttachment> = emptyList()
)

data class SlackAttachment(
    val color: String,
    val fields: List<SlackField> = emptyList()
)

data class SlackField(
    val title: String,
    val value: String,
    val short: Boolean = true
)

// 알림 메시지 (이벤트 퍼블리셔에서도 사용)
data class NotificationMessage(
    val message: String,
    val characterId: String,
    val timestamp: Long,
    val type: String,
    val metadata: Map<String, Any> = emptyMap()
)
