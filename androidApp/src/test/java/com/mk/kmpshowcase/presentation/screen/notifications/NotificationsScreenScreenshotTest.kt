package com.mk.kmpshowcase.presentation.screen.notifications

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
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
