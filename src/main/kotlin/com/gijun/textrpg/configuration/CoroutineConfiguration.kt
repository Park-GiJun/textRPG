package com.gijun.textrpg.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Hooks
import kotlin.coroutines.CoroutineContext

@Configuration
@EnableWebFlux
class CoroutineConfiguration {

    init {
        // Enable context propagation for coroutines
        Hooks.enableAutomaticContextPropagation()
    }

    @Bean
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Bean
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Bean
    fun unconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
}

// Custom coroutine scope for application
@Component
class ApplicationCoroutineScope(
    private val ioDispatcher: CoroutineDispatcher
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = ioDispatcher
}