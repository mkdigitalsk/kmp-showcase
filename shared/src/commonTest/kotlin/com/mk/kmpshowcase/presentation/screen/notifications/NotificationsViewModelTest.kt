package com.mk.kmpshowcase.presentation.screen.notifications

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.model.NotificationChannel
import com.mk.kmpshowcase.domain.repository.LocalNotificationService
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
import com.mk.kmpshowcase.domain.useCase.notifications.CancelAllNotificationsUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.GetPushPermissionStatusUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.LogPushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ObservePushNotificationsUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ObservePushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.RefreshPushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ShowLocalNotificationUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationsViewModelTest {

    private class FakePushNotificationService(
        initialStatus: PushPermissionStatus = PushPermissionStatus.NOT_DETERMINED,
        initialToken: String? = null
    ) : PushNotificationService {
        private val _token = MutableStateFlow(initialToken)
        override val token: StateFlow<String?> = _token
        override val notifications: Flow<Notification> = emptyFlow()
        override val deepLinks: Flow<String> = emptyFlow()

        var currentStatus = initialStatus
        var logTokenCalled = false
        var refreshTokenCalled = false

        override fun getPermissionStatus(): PushPermissionStatus = currentStatus
        override suspend fun refreshToken() { refreshTokenCalled = true }
        override fun logToken() { logTokenCalled = true }
    }

    private class FakeLocalNotificationService : LocalNotificationService {
        val shownNotifications = mutableListOf<Notification>()
        var cancelAllCalled = false

        override fun showNotification(notification: Notification) {
            shownNotifications.add(notification)
        }

        override fun cancelNotification(id: String) {}

        override fun cancelAllNotifications() {
            cancelAllCalled = true
        }
    }

    private fun createViewModel(
        pushService: FakePushNotificationService = FakePushNotificationService(),
        localService: FakeLocalNotificationService = FakeLocalNotificationService()
    ): NotificationsViewModel {
        return NotificationsViewModel(
            getPushPermissionStatusUseCase = GetPushPermissionStatusUseCase(pushService),
            observePushTokenUseCase = ObservePushTokenUseCase(pushService),
            observePushNotificationsUseCase = ObservePushNotificationsUseCase(pushService),
            refreshPushTokenUseCase = RefreshPushTokenUseCase(pushService),
            logPushTokenUseCase = LogPushTokenUseCase(pushService),
            showLocalNotificationUseCase = ShowLocalNotificationUseCase(localService),
            cancelAllNotificationsUseCase = CancelAllNotificationsUseCase(localService)
        )
    }

    @Test
    fun `default state has NOT_DETERMINED permission status`() {
        val viewModel = createViewModel()
        assertEquals(PushPermissionStatus.NOT_DETERMINED, viewModel.state.value.permissionStatus)
    }

    @Test
    fun `default state has no token`() {
        val viewModel = createViewModel()
        assertNull(viewModel.state.value.pushToken)
    }

    @Test
    fun `default state has no loading flags`() {
        val viewModel = createViewModel()
        assertEquals(false, viewModel.state.value.permissionLoading)
        assertEquals(false, viewModel.state.value.tokenRefreshing)
    }

    @Test
    fun `updatePermissionStatus updates state`() {
        val viewModel = createViewModel()

        viewModel.updatePermissionStatus(PushPermissionStatus.GRANTED)

        assertEquals(PushPermissionStatus.GRANTED, viewModel.state.value.permissionStatus)
    }

    @Test
    fun `updatePermissionStatus from GRANTED to DENIED`() {
        val pushService = FakePushNotificationService(initialStatus = PushPermissionStatus.GRANTED)
        val viewModel = createViewModel(pushService = pushService)

        viewModel.updatePermissionStatus(PushPermissionStatus.DENIED)

        assertEquals(PushPermissionStatus.DENIED, viewModel.state.value.permissionStatus)
    }

    @Test
    fun `default state has no lastSentNotification`() {
        val viewModel = createViewModel()
        assertNull(viewModel.state.value.lastSentNotification)
    }

    @Test
    fun `default state has no lastReceivedNotification`() {
        val viewModel = createViewModel()
        assertNull(viewModel.state.value.lastReceivedNotification)
    }
}
