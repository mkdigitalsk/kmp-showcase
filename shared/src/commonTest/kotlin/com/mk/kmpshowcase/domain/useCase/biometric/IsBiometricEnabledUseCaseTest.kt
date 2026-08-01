package com.mk.kmpshowcase.domain.useCase.biometric

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.BiometricRepository
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class IsBiometricEnabledUseCaseTest : BaseTest<IsBiometricEnabledUseCase>() {
    override lateinit var classUnderTest: IsBiometricEnabledUseCase

    private val biometricRepository: BiometricRepository = mock()

    override fun beforeEach() {
        classUnderTest = IsBiometricEnabledUseCase(biometricRepository)
    }

    @Test
    fun `invoke returns true when biometrics enabled`() = runTest {
        test(
            given = {
                everySuspend { biometricRepository.enabled() } returns true
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(true, result)
                verifySuspend { biometricRepository.enabled() }
            }
        )
    }

    @Test
    fun `invoke returns false when biometrics not enabled`() = runTest {
        test(
            given = {
                everySuspend { biometricRepository.enabled() } returns false
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(false, result)
            }
        )
    }
}
