package sk.mkdigital.kmpshowcase.util

import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SuspendRunCatchingTest {

    @Test
    fun `cancellation propagates instead of becoming a failed result`() = runTest {
        assertFailsWith<CancellationException> {
            suspendRunCatching { throw CancellationException("cancelled") }
        }
    }

    @Test
    fun `any other throwable becomes a failed result`() = runTest {
        val result = suspendRunCatching { throw IllegalStateException("boom") }

        assertTrue(result.isFailure)
        assertEquals("boom", result.exceptionOrNull()?.message)
    }

    @Test
    fun `a value becomes a successful result`() = runTest {
        assertEquals("ok", suspendRunCatching { "ok" }.getOrNull())
    }
}
