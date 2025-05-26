package com.gijun.textrpg.configuration

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.time.LocalDateTime
import java.time.ZoneId

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.gijun.textrpg.adapter.out.persistence"])
@EnableTransactionManagement
class R2dbcConfiguration(
    private val connectionFactory: ConnectionFactory
) : AbstractR2dbcConfiguration() {

    override fun connectionFactory(): ConnectionFactory = connectionFactory

    @Bean
    fun r2dbcEntityTemplate(connectionFactory: ConnectionFactory): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory)
    }

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters = mutableListOf<Any>()
        // Add custom converters here if needed
        // Example: converters.add(LocalDateTimeToTimestampConverter())
        return R2dbcCustomConversions(getStoreConversions(), converters)
    }
}

// Custom converter example
class LocalDateTimeToTimestampConverter : Converter<LocalDateTime, Long> {
    override fun convert(source: LocalDateTime): Long {
        return source.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}