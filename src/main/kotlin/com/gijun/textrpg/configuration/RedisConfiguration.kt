package com.gijun.textrpg.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, Any> {
        val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val jsonSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        val stringSerializer = StringRedisSerializer()

        val context = RedisSerializationContext
            .newSerializationContext<String, Any>(stringSerializer)
            .value(jsonSerializer)
            .hashKey(stringSerializer)
            .hashValue(jsonSerializer)
            .build()

        return ReactiveRedisTemplate(connectionFactory, context)
    }

    @Bean
    fun redisOperations(
        template: ReactiveRedisTemplate<String, Any>
    ): RedisOperations {
        return RedisOperations(template)
    }
}

// Wrapper class for common Redis operations
class RedisOperations(
    private val template: ReactiveRedisTemplate<String, Any>
) {
    suspend fun <T> get(key: String, clazz: Class<T>): T? {
        return template.opsForValue()
            .get(key)
            .map { it as T }
            .block()
    }

    suspend fun set(key: String, value: Any, ttl: Duration? = null) {
        if (ttl != null) {
            template.opsForValue()
                .set(key, value, ttl)
                .block()
        } else {
            template.opsForValue()
                .set(key, value)
                .block()
        }
    }

    suspend fun delete(key: String): Boolean {
        return template.delete(key)
            .map { it > 0 }
            .block() ?: false
    }

    suspend fun hasKey(key: String): Boolean {
        return template.hasKey(key)
            .block() ?: false
    }
}