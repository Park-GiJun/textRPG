package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.CreateCharacterCommand
import com.gijun.textrpg.application.port.out.CharacterCachePort
import com.gijun.textrpg.application.port.out.CharacterEventPublisher
import com.gijun.textrpg.application.port.out.CharacterRepository
import com.gijun.textrpg.domain.character.Character
import com.gijun.textrpg.domain.character.Health
import com.gijun.textrpg.domain.character.Stats
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CharacterServiceTest {

    private lateinit var characterRepository: CharacterRepository
    private lateinit var characterCache: CharacterCachePort
    private lateinit var eventPublisher: CharacterEventPublisher
    private lateinit var characterCreationService: CharacterCreationService
    private lateinit var characterQueryService: CharacterQueryService
    private lateinit var characterUpdateService: CharacterUpdateService
    private lateinit var characterService: CharacterService

    @BeforeEach
    fun setup() {
        characterRepository = mockk()
        characterCache = mockk()
        eventPublisher = mockk()
        
        characterCreationService = CharacterCreationService(characterRepository, characterCache, eventPublisher)
        characterQueryService = CharacterQueryService(characterRepository, characterCache)
        characterUpdateService = CharacterUpdateService(characterRepository, characterCache, eventPublisher, characterQueryService)
        
        characterService = CharacterService(characterCreationService, characterQueryService, characterUpdateService)
    }

    @Test
    fun `should create character successfully`() = runTest {
        // Given
        val command = CreateCharacterCommand(name = "TestHero")
        val expectedCharacter = createTestCharacter(name = "TestHero")

        coEvery { characterRepository.existsByName("TestHero") } returns false
        coEvery { characterRepository.save(any()) } returns expectedCharacter
        coEvery { characterCache.saveCharacter(any()) } just Runs
        coEvery { eventPublisher.publishCharacterCreated(any(), any(), any()) } just Runs

        // When
        val result = characterService.createCharacter(command)

        // Then
        assertNotNull(result)
        assertEquals("TestHero", result.name)
        assertEquals(1, result.level)

        coVerify { characterRepository.existsByName("TestHero") }
        coVerify { characterRepository.save(any()) }
        coVerify { characterCache.saveCharacter(any()) }
        coVerify { eventPublisher.publishCharacterCreated(any(), any(), any()) }
    }

    @Test
    fun `should throw exception when character name already exists`() = runTest {
        // Given
        val command = CreateCharacterCommand(name = "ExistingHero")

        coEvery { characterRepository.existsByName("ExistingHero") } returns true

        // When & Then
        assertThrows<CharacterAlreadyExistsException> {
            characterService.createCharacter(command)
        }

        coVerify { characterRepository.existsByName("ExistingHero") }
        coVerify(exactly = 0) { characterRepository.save(any()) }
    }

    @Test
    fun `should get character by id`() = runTest {
        // Given
        val characterId = "test-id"
        val character = createTestCharacter(id = characterId)

        coEvery { characterCache.getCharacter(characterId) } returns null
        coEvery { characterRepository.findById(characterId) } returns character
        coEvery { characterCache.saveCharacter(any()) } just Runs

        // When
        val result = characterService.getCharacter(characterId)

        // Then
        assertNotNull(result)
        assertEquals(characterId, result.id)

        coVerify { characterRepository.findById(characterId) }
        coVerify { characterCache.saveCharacter(any()) }
    }

    @Test
    fun `should stream all characters`() = runTest {
        // Given
        val characters = listOf(
            createTestCharacter(id = "1", name = "Hero1"),
            createTestCharacter(id = "2", name = "Hero2"),
            createTestCharacter(id = "3", name = "Hero3")
        )

        every { characterRepository.findAll() } returns flowOf(*characters.toTypedArray())
        coEvery { characterCache.saveCharacter(any()) } just Runs

        // When
        val result = characterService.getAllCharacters().toList()

        // Then
        assertEquals(3, result.size)
        assertEquals("Hero1", result[0].name)
        assertEquals("Hero2", result[1].name)
        assertEquals("Hero3", result[2].name)
    }

    private fun createTestCharacter(
        id: String = "test-id",
        name: String = "TestCharacter",
        level: Int = 1
    ): Character {
        return Character(
            id = id,
            name = name,
            level = level,
            health = Health(current = 100, max = 100),
            stats = Stats(
                strength = 10,
                dexterity = 10,
                intelligence = 10,
                luck = 5
            )
        )
    }
}
