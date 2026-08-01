package com.mk.kmpshowcase.presentation.screen.settings

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class SettingsScreenScreenshotTest(
    stateHolder: StateHolder<SettingsState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<SettingsState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(SettingsScreenPreviewParams())
    }

    @Test
    fun settingsScreen() {
        screenshot {
            SettingsScreen(state = state)
        }
    }
}
