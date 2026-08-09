package sk.mkdigital.kmpshowcase.presentation.screen.signIn

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class SignInScreenScreenshotTest(
    stateHolder: StateHolder<SignInUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<SignInUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(SignInScreenPreviewParams())
    }

    @Test
    fun signInScreen() {
        screenshot {
            SignInScreen(state = state)
        }
    }
}
