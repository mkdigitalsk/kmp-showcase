package com.mk.kmpshowcase.presentation.screen.database

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class DatabaseScreenScreenshotTest(
    stateHolder: StateHolder<DatabaseUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<DatabaseUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(DatabaseScreenPreviewParams())
    }

    @Test
    fun databaseScreen() {
        screenshot {
            DatabaseScreen(state = state)
        }
    }
}
