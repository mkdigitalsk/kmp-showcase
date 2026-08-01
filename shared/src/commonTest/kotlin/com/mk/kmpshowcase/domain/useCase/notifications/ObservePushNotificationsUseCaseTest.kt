package com.mk.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.model.NotificationChannel
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class ObservePushNotificationsUseCaseTest : BaseTest<ObservePushNotificationsUseCase>() {
    override lateinit var classUnderTest: ObservePushNotificationsUseCase

    private val pushNotificationService: PushNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = ObservePushNotificationsUseCase(pushNotificationService)
    }

    @Test
    fun `invoke returns flow of notifications`() = runTest {
        var notification: Notification?= null
        test(
            given = {
                notification = Notification(
                    id = "1",
                    title = "Title",
                    message = "Message",
                    channel = NotificationChannel.GENERAL
                )
                every { pushNotificationService.notifications } returns flowOf(notification)
            },
            whenAction = {
                classUnderTest().first()
            },
            then = {
                assertEquals(notification, it)
            }
        )
    }
}
