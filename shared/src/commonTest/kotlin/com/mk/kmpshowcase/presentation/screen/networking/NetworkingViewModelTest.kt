package com.mk.kmpshowcase.presentation.screen.networking

import app.cash.turbine.test
import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.repository.UserRepository
import com.mk.kmpshowcase.domain.useCase.GetUsersUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModelTest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkingViewModelTest : BaseViewModelTest() {

    private class FakeUserRepository(
        private val users: List<User> = emptyList(),
        private val error: Throwable? = null,
        private val gate: CompletableDeferred<Unit>? = null,
    ) : UserRepository {
        override suspend fun getUser(id: Long): User = users.first { it.id == id }
        override suspend fun getUsers(): List<User> {
            gate?.await()
            error?.let { throw it }
            return users
        }
    }

    private fun createViewModel(
        users: List<User> = emptyList(),
        error: Throwable? = null,
        gate: CompletableDeferred<Unit>? = null,
    ) = NetworkingViewModel(GetUsersUseCase(FakeUserRepository(users, error, gate)))

    private val alice = User(id = 1, email = "alice@mk.sk", name = "Alice")

    @Test
    fun `fetchUsers maps domain users into UI models`() = runTest {
        val viewModel = createViewModel(users = listOf(alice))

        viewModel.fetchUsers()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf(UserUiModel(id = 1, name = "Alice", email = "alice@mk.sk")), state.users)
    }

    @Test
    fun `fetchUsers failure sets error and stops loading`() = runTest {
        val viewModel = createViewModel(error = RuntimeException("boom"))

        viewModel.fetchUsers()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.users.isEmpty())
    }

    @Test
    fun `fetchUsers emits loading then success`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val viewModel = createViewModel(users = listOf(alice), gate = gate)

        viewModel.state.test {
            assertEquals(NetworkingUiState(), awaitItem())   // initial

            viewModel.fetchUsers()
            assertTrue(awaitItem().isLoading)                // loading (repo suspended on the gate)

            gate.complete(Unit)
            val success = awaitItem()                        // success
            assertFalse(success.isLoading)
            assertEquals(listOf(UserUiModel(id = 1, name = "Alice", email = "alice@mk.sk")), success.users)
        }
    }
}
