package sk.mkdigital.kmpshowcase.presentation.screen.register

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class RegisterScreenScreenshotTest(
    stateHolder: StateHolder<RegisterUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<RegisterUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(RegisterScreenPreviewParams())
    }

    @Test
    fun registerScreen() {
        screenshot {
            RegisterScreen(state = state)
        }
    }
}
