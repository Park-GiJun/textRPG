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
    private lateinit var characterService: CharacterService

    @BeforeEach
    fun setup() {
        characterRepository = mockk()
        characterCache = mockk()
        eventPublisher = mockk()
        characterService = CharacterService(characterRepository, characterCache, eventPublisher)
    }

    @Test
    fun `should create character successfully`() = runTest {
        // Given
        val command = CreateCharacterCommand(name = "TestHero")
        val expectedCharacter = createTestCharacter(name = "TestHero")

        coEvery { characterRepository.findByName("TestHero") } returns null
        coEvery { characterRepository.save(any()) } returns expectedCharacter
        coEvery { characterCache.saveCharacter(any()) } just Runs
        coEvery { eventPublisher.publishCharacterCreated(any(), any()) } just Runs

        // When
        val result = characterService.createCharacter(command)

        // Then
        assertNotNull(result)
        assertEquals("TestHero", result.name)
        assertEquals(1, result.level)

        coVerify { characterRepository.findByName("TestHero") }
        coVerify { characterRepository.save(any()) }
        coVerify { characterCache.saveCharacter(expectedCharacter) }
        coVerify { eventPublisher.publishCharacterCreated(expectedCharacter.id, "TestHero") }
    }

    @Test
    fun `should throw exception when character name already exists`() = runTest {
        // Given
        val command = CreateCharacterCommand(name = "ExistingHero")
        val existingCharacter = createTestCharacter(name = "ExistingHero")

        coEvery { characterRepository.findByName("ExistingHero") } returns existingCharacter

        // When & Then
        assertThrows<CharacterAlreadyExistsException> {
            characterService.createCharacter(command)
        }

        coVerify { characterRepository.findByName("ExistingHero") }
        coVerify(exactly = 0) { characterRepository.save(any()) }
    }

    @Test
    fun `should get character from cache first`() = runTest {
        // Given
        val characterId = "test-id"
        val cachedCharacter = createTestCharacter(id = characterId)

        coEvery { characterCache.getCharacter(characterId) } returns cachedCharacter

        // When
        val result = characterService.getCharacter(characterId)

        // Then
        assertNotNull(result)
        assertEquals(characterId, result.id)

        coVerify { characterCache.getCharacter(characterId) }
        coVerify(exactly = 0) { characterRepository.findById(any()) }
    }

    @Test
    fun `should get character from repository when not in cache`() = runTest {
        // Given
        val characterId = "test-id"
        val character = createTestCharacter(id = characterId)

        coEvery { characterCache.getCharacter(characterId) } returns null
        coEvery { characterRepository.findById(characterId) } returns character
        coEvery { characterCache.saveCharacter(character) } just Runs

        // When
        val result = characterService.getCharacter(characterId)

        // Then
        assertNotNull(result)
        assertEquals(characterId, result.id)

        coVerify { characterCache.getCharacter(characterId) }
        coVerify { characterRepository.findById(characterId) }
        coVerify { characterCache.saveCharacter(character) }
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

        coVerify(exactly = 3) { characterCache.saveCharacter(any()) }
    }

    @Test
    fun `should level up character when gaining enough experience`() = runTest {
        // Given
        val characterId = "test-id"
        val character = createTestCharacter(id = characterId, level = 1, experience = 500)
        val leveledUpCharacter = character.copy(level = 2, experience = 1500)

        coEvery { characterCache.getCharacter(characterId) } returns null
        coEvery { characterRepository.findById(characterId) } returns character
        coEvery { characterCache.saveCharacter(any()) } just Runs
        coEvery { characterRepository.save(any()) } returns leveledUpCharacter
        coEvery { eventPublisher.publishCharacterLeveledUp(any(), any(), any()) } just Runs

        // When
        val result = characterService.gainExperience(characterId, 1000)

        // Then
        assertEquals(2, result.level)
        assertEquals(1500, result.experience)

        coVerify { eventPublisher.publishCharacterLeveledUp(characterId, 1, 2) }
    }

    private fun createTestCharacter(
        id: String = "test-id",
        name: String = "TestCharacter",
        level: Int = 1,
        experience: Long = 0
    ): Character {
        return Character(
            id = id,
            name = name,
            level = level,
            experience = experience,
            health = Health(current = 100, max = 100),
            stats = Stats(
                strength = 10,
                dexterity = 10,
                intelligence = 10,
                vitality = 10
            )
        )
    }
}
