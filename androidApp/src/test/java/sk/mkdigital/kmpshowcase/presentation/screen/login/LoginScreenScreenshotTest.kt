package sk.mkdigital.kmpshowcase.presentation.screen.login

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class LoginScreenScreenshotTest(
    stateHolder: StateHolder<LoginUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<LoginUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(LoginScreenPreviewParams())
    }

    @Test
    fun loginScreen() {
        screenshot {
            LoginScreen(state = state)
        }
    }
}
