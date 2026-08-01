package com.mk.kmpshowcase.presentation.screen.scanner

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
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
