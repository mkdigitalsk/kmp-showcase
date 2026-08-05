package sk.mkdigital.kmpshowcase.presentation.screen.networking

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class NetworkingScreenScreenshotTest(
    stateHolder: StateHolder<NetworkingUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<NetworkingUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(NetworkingScreenPreviewParams())
    }

    @Test
    fun networkingScreen() {
        screenshot {
            NetworkingScreen(state = state)
        }
    }
}
