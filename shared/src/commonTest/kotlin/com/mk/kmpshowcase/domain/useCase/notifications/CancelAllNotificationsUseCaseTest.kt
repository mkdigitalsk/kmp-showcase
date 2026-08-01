package com.mk.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.LocalNotificationService
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test

class CancelAllNotificationsUseCaseTest : BaseTest<CancelAllNotificationsUseCase>() {
    override lateinit var classUnderTest: CancelAllNotificationsUseCase

    private val localNotificationService: LocalNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = CancelAllNotificationsUseCase(localNotificationService)
    }

    @Test
    fun `invoke calls service cancelAllNotifications`() = runTest {
        test(
            given = {
                everySuspend { localNotificationService.cancelAllNotifications() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { localNotificationService.cancelAllNotifications() }
            }
        )
    }
}
