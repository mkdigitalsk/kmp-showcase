package mk.digital.kmpshowcase.presentation.screen.networking

import mk.digital.kmpshowcase.domain.model.Address
import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.domain.repository.UserRepository
import mk.digital.kmpshowcase.domain.useCase.GetUsersUseCase
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
        override suspend fun getUser(id: Int): User {
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

    private fun createTestUser(id: Int = 1, name: String = "John Doe") = User(
        id = id,
        name = name,
        email = "john@example.com",
        address = Address(
            city = "New York",
            street = "Main St",
            suite = "Apt 1",
            zipcode = "10001"
        )
    )

    // === Default State Tests ===

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

    // === NetworkingUiState Tests ===

    @Test
    fun `NetworkingUiState default values are correct`() {
        val state = NetworkingUiState()
        assertFalse(state.isLoading)
        assertTrue(state.users.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `NetworkingUiState can hold users`() {
        val users = listOf(createTestUser(1), createTestUser(2))
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
