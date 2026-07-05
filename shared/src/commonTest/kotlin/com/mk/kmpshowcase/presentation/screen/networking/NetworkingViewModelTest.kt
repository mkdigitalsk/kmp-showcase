package com.mk.kmpshowcase.presentation.screen.networking

import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.repository.UserRepository
import com.mk.kmpshowcase.domain.useCase.GetUsersUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkingViewModelTest {

    private class FakeUserRepository(
        private val users: List<User> = emptyList(),
        private val shouldThrow: Boolean = false
    ) : UserRepository {
        override suspend fun getUser(id: Long): User {
            if (shouldThrow) throw RuntimeException("Network error")
            return users.first { it.id == id }
        }

        override suspend fun getUsers(): List<User> {
            if (shouldThrow) throw RuntimeException("Network error")
            return users
        }
    }

    private fun createViewModel(
        users: List<User> = emptyList(),
        shouldThrow: Boolean = false
    ): NetworkingViewModel {
        val repository = FakeUserRepository(users, shouldThrow)
        return NetworkingViewModel(GetUsersUseCase(repository))
    }

    private fun createTestUserUiModel(id: Long = 1, name: String = "John Doe") = UserUiModel(
        id = id,
        name = name,
        email = "john@example.com",
    )


    @Test
    fun `default state has empty users list`() {
        val viewModel = createViewModel()
        assertTrue(viewModel.state.value.users.isEmpty())
    }

    @Test
    fun `default state is not loading`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `default state has no error`() {
        val viewModel = createViewModel()
        assertNull(viewModel.state.value.error)
    }


    @Test
    fun `NetworkingUiState default values are correct`() {
        val state = NetworkingUiState()
        assertFalse(state.isLoading)
        assertTrue(state.users.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `NetworkingUiState can hold users`() {
        val users = listOf(createTestUserUiModel(1), createTestUserUiModel(2))
        val state = NetworkingUiState(users = users)
        assertEquals(2, state.users.size)
    }

    @Test
    fun `NetworkingUiState can hold error`() {
        val state = NetworkingUiState(error = "Network error")
        assertEquals("Network error", state.error)
    }

    @Test
    fun `NetworkingUiState can have loading state`() {
        val state = NetworkingUiState(isLoading = true)
        assertTrue(state.isLoading)
    }
}
