package mk.digital.kmpshowcase.presentation.screen.notifications

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import mk.digital.kmpshowcase.domain.model.Notification
import mk.digital.kmpshowcase.domain.model.NotificationChannel
import mk.digital.kmpshowcase.domain.repository.LocalNotificationService
import mk.digital.kmpshowcase.domain.repository.PushNotificationService
import mk.digital.kmpshowcase.domain.repository.PushPermissionStatus
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
        override suspend fun requestPermission(): PushPermissionStatus = currentStatus
        override suspend fun refreshToken() { refreshTokenCalled = true }
        override fun logToken() { logTokenCalled = true }

        fun updateToken(token: String) { _token.value = token }
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
        return NotificationsViewModel(pushService, localService)
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
    fun `logToken calls push service`() {
        val pushService = FakePushNotificationService()
        val viewModel = createViewModel(pushService = pushService)

        viewModel.logToken()

        assertEquals(true, pushService.logTokenCalled)
    }

    @Test
    fun `sendReminderNotification shows notification with REMINDERS channel`() {
        val localService = FakeLocalNotificationService()
        val viewModel = createViewModel(localService = localService)

        viewModel.sendReminderNotification("Test Title", "Test Message")

        assertEquals(1, localService.shownNotifications.size)
        val notification = localService.shownNotifications.first()
        assertEquals("Test Title", notification.title)
        assertEquals("Test Message", notification.message)
        assertEquals(NotificationChannel.REMINDERS, notification.channel)
    }

    @Test
    fun `sendPromoNotification shows notification with PROMOTIONS channel`() {
        val localService = FakeLocalNotificationService()
        val viewModel = createViewModel(localService = localService)

        viewModel.sendPromoNotification("Promo Title", "Promo Message")

        assertEquals(1, localService.shownNotifications.size)
        val notification = localService.shownNotifications.first()
        assertEquals("Promo Title", notification.title)
        assertEquals("Promo Message", notification.message)
        assertEquals(NotificationChannel.PROMOTIONS, notification.channel)
    }

    @Test
    fun `sendReminderNotification updates lastSentNotification in state`() {
        val viewModel = createViewModel()

        viewModel.sendReminderNotification("Reminder", "Don't forget!")

        assertEquals("Reminder", viewModel.state.value.lastSentNotification)
    }

    @Test
    fun `sendPromoNotification updates lastSentNotification in state`() {
        val viewModel = createViewModel()

        viewModel.sendPromoNotification("Sale!", "50% off")

        assertEquals("Sale!", viewModel.state.value.lastSentNotification)
    }

    @Test
    fun `cancelAllNotifications calls local service`() {
        val localService = FakeLocalNotificationService()
        val viewModel = createViewModel(localService = localService)

        viewModel.cancelAllNotifications()

        assertEquals(true, localService.cancelAllCalled)
    }

    @Test
    fun `cancelAllNotifications clears lastSentNotification`() {
        val viewModel = createViewModel()
        viewModel.sendReminderNotification("Test", "Message")

        viewModel.cancelAllNotifications()

        assertNull(viewModel.state.value.lastSentNotification)
    }

    @Test
    fun `multiple notifications have unique IDs`() {
        val localService = FakeLocalNotificationService()
        val viewModel = createViewModel(localService = localService)

        viewModel.sendReminderNotification("First", "Message")
        viewModel.sendReminderNotification("Second", "Message")
        viewModel.sendPromoNotification("Third", "Message")

        assertEquals(3, localService.shownNotifications.size)
        val ids = localService.shownNotifications.map { it.id }.toSet()
        assertEquals(3, ids.size) // All IDs should be unique
    }
}
