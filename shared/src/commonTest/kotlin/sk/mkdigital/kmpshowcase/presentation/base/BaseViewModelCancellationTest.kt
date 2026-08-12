package sk.mkdigital.kmpshowcase.presentation.base

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.exceptions.base.BaseException
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelCancellationTest : BaseViewModelTest() {

    private data class TestState(val value: Int = 0)

    private class TestViewModel : BaseViewModel<TestState>(TestState()) {
        val started = CompletableDeferred<Unit>()
        var errorReported = false
        var succeeded = false

        fun runForever() = execute(
            action = {
                started.complete(Unit)
                CompletableDeferred<Unit>().await()
            },
            onSuccess = { succeeded = true },
            onError = { errorReported = true },
        )
    }

    // Cancelling a ViewModel job must unwind it, not surface to the user as a failed operation.
    @Test
    fun `cancelling an operation reports neither success nor error`() = runTest {
        val viewModel = TestViewModel()

        val job = viewModel.runForever()
        viewModel.started.await()
        job.cancel()

        assertTrue(job.isCancelled)
        assertFalse(viewModel.errorReported)
        assertFalse(viewModel.succeeded)
    }

    @Test
    fun `a real failure still reports an error`() = runTest {
        val viewModel = object : BaseViewModel<TestState>(TestState()) {
            var reported: BaseException? = null

            fun boom() = execute(
                action = { error("boom") },
                onError = { reported = it },
            )
        }

        viewModel.boom().join()

        assertTrue(viewModel.reported != null)
    }
}
