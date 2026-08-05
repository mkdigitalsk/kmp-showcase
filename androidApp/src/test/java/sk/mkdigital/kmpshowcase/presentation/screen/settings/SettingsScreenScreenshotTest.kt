package sk.mkdigital.kmpshowcase.presentation.screen.settings

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
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
