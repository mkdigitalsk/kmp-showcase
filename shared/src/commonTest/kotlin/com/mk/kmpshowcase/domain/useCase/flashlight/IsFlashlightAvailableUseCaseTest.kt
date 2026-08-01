package com.mk.kmpshowcase.domain.useCase.flashlight

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.FlashlightRepository
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class IsFlashlightAvailableUseCaseTest : BaseTest<IsFlashlightAvailableUseCase>() {
    override lateinit var classUnderTest: IsFlashlightAvailableUseCase

    private val flashlightRepository: FlashlightRepository = mock()

    override fun beforeEach() {
        classUnderTest = IsFlashlightAvailableUseCase(flashlightRepository)
    }

    @Test
    fun `invoke returns true when flashlight is available`() = runTest {
        test(
            given = {
                everySuspend { flashlightRepository.isAvailable() } returns true
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(true, result)
                verifySuspend { flashlightRepository.isAvailable() }
            }
        )
    }

    @Test
    fun `invoke returns false when flashlight is not available`() = runTest {
        test(
            given = {
                everySuspend { flashlightRepository.isAvailable() } returns false
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
