package com.mk.kmpshowcase.presentation.screen.home

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
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
