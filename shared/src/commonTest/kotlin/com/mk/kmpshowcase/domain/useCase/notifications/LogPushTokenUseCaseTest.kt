package com.mk.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test

class LogPushTokenUseCaseTest : BaseTest<LogPushTokenUseCase>() {
    override lateinit var classUnderTest: LogPushTokenUseCase

    private val pushNotificationService: PushNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = LogPushTokenUseCase(pushNotificationService)
    }

    @Test
    fun `invoke calls service logToken`() = runTest {
        test(
            given = {
                everySuspend { pushNotificationService.logToken() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { pushNotificationService.logToken() }
            }
        )
    }
}
