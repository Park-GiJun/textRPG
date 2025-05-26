package com.gijun.textrpg.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Mono

@Configuration
class WebSocketConfiguration {

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

    @Bean
    fun handlerMapping(gameWebSocketHandler: GameWebSocketHandler): HandlerMapping {
        val map = mapOf(
            "/ws/game" to gameWebSocketHandler
            // Add more WebSocket endpoints here
        )
        
        return SimpleUrlHandlerMapping().apply {
            urlMap = map
            order = -1 // Before other handlers
        }
    }
}

// Example WebSocket Handler
@Component
class GameWebSocketHandler : WebSocketHandler {
    
    override fun handle(session: WebSocketSession): Mono<Void> {
        // Send welcome message
        val welcomeMessage = session.textMessage("""{"type":"connected","message":"Welcome to TextRPG!"}""")
        
        return session.send(Mono.just(welcomeMessage))
            .and(
                session.receive()
                    .map { it.payloadAsText }
                    .map { message ->
                        // Echo back the message
                        session.textMessage("""{"type":"echo","message":"$message"}""")
                    }
                    .let { session.send(it) }
            )
    }
}