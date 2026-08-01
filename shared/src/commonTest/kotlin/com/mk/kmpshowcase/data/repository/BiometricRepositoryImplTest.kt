package com.mk.kmpshowcase.data.repository

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.data.client.BiometricClient
import com.mk.kmpshowcase.domain.model.BiometricResult
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiometricRepositoryImplTest : BaseTest<BiometricRepositoryImpl>() {
    override lateinit var classUnderTest: BiometricRepositoryImpl

    private val biometricClient: BiometricClient = mock()

    override fun beforeEach() {
        classUnderTest = BiometricRepositoryImpl(biometricClient)
    }

    @Test
    fun `enabled returns true when biometrics available`() {
        every { biometricClient.enabled() } returns true

        val result = classUnderTest.enabled()

        assertTrue(result)
        verify { biometricClient.enabled() }
    }

    @Test
    fun `enabled returns false when biometrics not available`() {
        every { biometricClient.enabled() } returns false

        val result = classUnderTest.enabled()

        assertFalse(result)
    }

    @Test
    fun `authenticate returns Success on successful authentication`() = runTest {
        test(
            given = {
                everySuspend { biometricClient.authenticate() } returns BiometricResult.Success
            },
            whenAction = {
                classUnderTest.authenticate()
            },
            then = { result ->
                assertEquals(BiometricResult.Success, result)
                verifySuspend { biometricClient.authenticate() }
            }
        )
    }

    @Test
    fun `authenticate returns Cancelled when user cancels`() = runTest {
        test(
            given = {
                everySuspend { biometricClient.authenticate() } returns BiometricResult.Cancelled
            },
            whenAction = {
                classUnderTest.authenticate()
            },
            then = { result ->
                assertEquals(BiometricResult.Cancelled, result)
            }
        )
    }

    @Test
    fun `authenticate returns NotAvailable when biometrics not available`() = runTest {
        test(
            given = {
                everySuspend { biometricClient.authenticate() } returns BiometricResult.NotAvailable
            },
            whenAction = {
                classUnderTest.authenticate()
            },
            then = { result ->
                assertEquals(BiometricResult.NotAvailable, result)
            }
        )
    }

    @Test
    fun `authenticate returns SystemError with message`() = runTest {
        val errorMessage = "Authentication failed"

        test(
            given = {
                everySuspend { biometricClient.authenticate() } returns BiometricResult.SystemError(errorMessage)
            },
            whenAction = {
                classUnderTest.authenticate()
            },
            then = { result ->
                assertEquals(BiometricResult.SystemError(errorMessage), result)
            }
        )
    }

    @Test
    fun `authenticate throws exception when client fails`() = runTest {
        val exception = RuntimeException("Client error")

        everySuspend { biometricClient.authenticate() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest.authenticate()
        }
    }
}
