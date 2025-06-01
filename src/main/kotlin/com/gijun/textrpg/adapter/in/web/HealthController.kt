package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.application.port.out.CharacterCachePort
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/health")
class HealthController(
    private val databaseClient: DatabaseClient,
    private val redisTemplate: ReactiveRedisTemplate<String, Any>,
    private val characterCache: CharacterCachePort
) {

    @GetMapping
    suspend fun health(): HealthCheckResponse {
        return HealthCheckResponse(
            status = "UP",
            timestamp = LocalDateTime.now(),
            services = mapOf(
                "api" to "UP",
                "database" to checkDatabase(),
                "redis" to checkRedis(),
                "cache" to checkCache()
            )
        )
    }

    private suspend fun checkDatabase(): String {
        return try {
            databaseClient.sql("SELECT 1")
                .fetch()
                .first()
                .map { "UP" }
                .onErrorReturn("DOWN")
                .block() ?: "DOWN"
        } catch (e: Exception) {
            "DOWN"
        }
    }

    private suspend fun checkRedis(): String {
        return try {
            redisTemplate.connectionFactory.reactiveConnection
                .ping()
                .map { "UP" }
                .onErrorReturn("DOWN")
                .block() ?: "DOWN"
        } catch (e: Exception) {
            "DOWN"
        }
    }

    private suspend fun checkCache(): String {
        return try {
            // 캐시에 테스트 데이터 저장/조회해서 정상 작동 확인
            val testKey = "health-check-${System.currentTimeMillis()}"
            redisTemplate.opsForValue()
                .set(testKey, "test", Duration.ofSeconds(5))
                .block()
            
            val result = redisTemplate.opsForValue()
                .get(testKey)
                .block()
            
            redisTemplate.delete(testKey).block()
            
            if (result == "test") "UP" else "DOWN"
        } catch (e: Exception) {
            "DOWN"
        }
    }
}

data class HealthCheckResponse(
    val status: String,
    val timestamp: LocalDateTime,
    val services: Map<String, String>
)

// Custom Health Indicator for Actuator
@org.springframework.stereotype.Component
class DatabaseHealthIndicator(
    private val databaseClient: DatabaseClient
) : ReactiveHealthIndicator {
    
    override fun health(): Mono<Health> {
        return databaseClient.sql("SELECT 1")
            .fetch()
            .first()
            .map { Health.up().withDetail("database", "MySQL").build() }
            .onErrorResume { ex ->
                Mono.just(Health.down(ex).withDetail("database", "MySQL").build())
            }
    }
}

@org.springframework.stereotype.Component
class RedisHealthIndicator(
    private val redisTemplate: ReactiveRedisTemplate<String, Any>
) : ReactiveHealthIndicator {
    
    override fun health(): Mono<Health> {
        return redisTemplate.connectionFactory.reactiveConnection
            .ping()
            .map { Health.up().withDetail("redis", "Available").build() }
            .onErrorResume { ex ->
                Mono.just(Health.down(ex).withDetail("redis", "Unavailable").build())
            }
    }
}
