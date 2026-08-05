package sk.mkdigital.kmpshowcase.presentation.screen.home

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class HomeScreenScreenshotTest(
    stateHolder: StateHolder<HomeUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<HomeUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(HomeScreenPreviewParams())
    }

    @Test
    fun homeScreen() {
        screenshot {
            HomeScreen(state = state)
        }
    }
}
