package com.mk.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RefreshPushTokenUseCaseTest : BaseTest<RefreshPushTokenUseCase>() {
    override lateinit var classUnderTest: RefreshPushTokenUseCase

    private val pushNotificationService: PushNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = RefreshPushTokenUseCase(pushNotificationService)
    }

    @Test
    fun `invoke calls service refreshToken`() = runTest {
        test(
            given = {
                everySuspend { pushNotificationService.refreshToken() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { pushNotificationService.refreshToken() }
            }
        )
    }

    @Test
    fun `invoke throws exception when service fails`() = runTest {
        val exception = RuntimeException("Token refresh error")

        everySuspend { pushNotificationService.refreshToken() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
