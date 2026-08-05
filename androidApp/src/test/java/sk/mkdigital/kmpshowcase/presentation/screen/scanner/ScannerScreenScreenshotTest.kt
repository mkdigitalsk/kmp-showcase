package sk.mkdigital.kmpshowcase.presentation.screen.scanner

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class ScannerScreenScreenshotTest(
    stateHolder: StateHolder<ScannerUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<ScannerUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(ScannerScreenPreviewParams())
    }

    @Test
    fun scannerScreen() {
        screenshot {
            ScannerScreen(state = state)
        }
    }
}
