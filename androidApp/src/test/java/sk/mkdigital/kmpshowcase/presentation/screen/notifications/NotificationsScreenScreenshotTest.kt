package sk.mkdigital.kmpshowcase.presentation.screen.notifications

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class NotificationsScreenScreenshotTest(
    stateHolder: StateHolder<NotificationsUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<NotificationsUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(NotificationsScreenPreviewParams())
    }

    @Test
    fun notificationsScreen() {
        screenshot {
            NotificationsScreen(state = state)
        }
    }
}
