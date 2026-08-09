package sk.mkdigital.kmpshowcase.presentation.screen.signUp

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class SignUpScreenScreenshotTest(
    stateHolder: StateHolder<SignUpUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<SignUpUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(SignUpScreenPreviewParams())
    }

    @Test
    fun signUpScreen() {
        screenshot {
            SignUpScreen(state = state)
        }
    }
}
