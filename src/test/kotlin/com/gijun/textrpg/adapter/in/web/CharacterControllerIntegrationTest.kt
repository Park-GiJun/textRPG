package com.gijun.textrpg.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.textrpg.application.service.CharacterService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers

@WebFluxTest(CharacterController::class)
@TestPropertySource(locations = ["classpath:application-test.yml"])
@Import(CharacterControllerIntegrationTest.TestConfig::class)
@Testcontainers
class CharacterControllerIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create character successfully`() {
        // Given
        val createRequest = CreateCharacterRequest(
            name = "TestHero",
            strength = 15,
            dexterity = 12,
            intelligence = 8,
            luck = 10
        )

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/characters")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.name").isEqualTo("TestHero")
            .jsonPath("$.level").isEqualTo(1)
            .jsonPath("$.stats.strength").isEqualTo(15)
            .jsonPath("$.stats.dexterity").isEqualTo(12)
            .jsonPath("$.stats.intelligence").isEqualTo(8)
            .jsonPath("$.stats.luck").isEqualTo(10)
            .jsonPath("$.health.current").exists()
            .jsonPath("$.health.max").exists()
            .jsonPath("$.id").exists()
    }

    @Test
    fun `should validate character name`() {
        // Given
        val invalidRequest = CreateCharacterRequest(name = "")

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/characters")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest
    }

    @TestConfiguration
    class TestConfig {
        // Mock 설정은 필요에 따라 추가
        // @Bean
        // @Primary
        // fun characterService(): CharacterService = mockk()
    }
}
