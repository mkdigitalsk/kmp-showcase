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

class TurnOffFlashlightUseCaseTest : BaseTest<TurnOffFlashlightUseCase>() {
    override lateinit var classUnderTest: TurnOffFlashlightUseCase

    private val flashlightRepository: FlashlightRepository = mock()

    override fun beforeEach() {
        classUnderTest = TurnOffFlashlightUseCase(flashlightRepository)
    }

    @Test
    fun `invoke returns true when turn off succeeds`() = runTest {
        test(
            given = {
                everySuspend { flashlightRepository.turnOff() } returns true
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(true, result)
                verifySuspend { flashlightRepository.turnOff() }
            }
        )
    }

    @Test
    fun `invoke returns false when turn off fails`() = runTest {
        test(
            given = {
                everySuspend { flashlightRepository.turnOff() } returns false
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
