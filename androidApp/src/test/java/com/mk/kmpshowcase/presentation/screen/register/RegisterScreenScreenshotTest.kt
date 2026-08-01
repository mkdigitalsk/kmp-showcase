package com.mk.kmpshowcase.presentation.screen.register

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
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
