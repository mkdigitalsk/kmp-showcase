package mk.digital.kmpshowcase.presentation.screen.storage

import kotlin.test.Test
import kotlin.test.assertEquals

class StorageViewModelTest {

    // === StorageUiState Tests ===

    @Test
    fun `StorageUiState default values are zero`() {
        val state = StorageUiState()
        assertEquals(0, state.sessionCounter)
        assertEquals(0, state.persistentCounter)
    }

    @Test
    fun `StorageUiState can be created with custom values`() {
        val state = StorageUiState(sessionCounter = 5, persistentCounter = 10)
        assertEquals(5, state.sessionCounter)
        assertEquals(10, state.persistentCounter)
    }

    @Test
    fun `StorageUiState copy works correctly`() {
        val state = StorageUiState(sessionCounter = 5, persistentCounter = 10)
        val copied = state.copy(sessionCounter = 7)
        assertEquals(7, copied.sessionCounter)
        assertEquals(10, copied.persistentCounter)
    }

    @Test
    fun `StorageUiState copy preserves unchanged values`() {
        val state = StorageUiState(sessionCounter = 5, persistentCounter = 10)
        val copied = state.copy(persistentCounter = 15)
        assertEquals(5, copied.sessionCounter)
        assertEquals(15, copied.persistentCounter)
    }

    @Test
    fun `StorageUiState equality works`() {
        val state1 = StorageUiState(sessionCounter = 5, persistentCounter = 10)
        val state2 = StorageUiState(sessionCounter = 5, persistentCounter = 10)
        assertEquals(state1, state2)
    }
}
