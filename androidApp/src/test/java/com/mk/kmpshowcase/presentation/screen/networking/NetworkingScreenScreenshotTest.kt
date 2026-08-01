package com.mk.kmpshowcase.presentation.screen.networking

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
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
