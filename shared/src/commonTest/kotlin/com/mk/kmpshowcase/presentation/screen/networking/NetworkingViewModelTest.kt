package com.mk.kmpshowcase.presentation.screen.networking

import app.cash.turbine.test
import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.useCase.GetUsersUseCase
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.presentation.base.BaseViewModelTest
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkingViewModelTest : BaseViewModelTest() {

    private val getUsersUseCase = mock<GetUsersUseCase>()

    private val alice = User(id = 1, email = "alice@mk.sk", name = "Alice")

    @Test
    fun `fetchUsers maps domain users into UI models`() = runTest {
        everySuspend { getUsersUseCase(None) } returns listOf(alice)

        val viewModel = NetworkingViewModel(getUsersUseCase)
        viewModel.fetchUsers()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(listOf(UserUiModel(id = 1, name = "Alice", email = "alice@mk.sk")), state.users)
    }

    @Test
    fun `fetchUsers failure sets error and stops loading`() = runTest {
        everySuspend { getUsersUseCase(None) } throws RuntimeException("boom")

        val viewModel = NetworkingViewModel(getUsersUseCase)
        viewModel.fetchUsers()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `fetchUsers emits loading then success`() = runTest {
        val gate = CompletableDeferred<Unit>()
        everySuspend { getUsersUseCase(None) } calls { gate.await(); listOf(alice) }

        val viewModel = NetworkingViewModel(getUsersUseCase)
        viewModel.state.test {
            assertEquals(NetworkingUiState(), awaitItem())   // initial
            viewModel.fetchUsers()
            assertTrue(awaitItem().isLoading)                // loading (use case suspended on the gate)
            gate.complete(Unit)
            assertFalse(awaitItem().isLoading)               // success
        }
    }
}
