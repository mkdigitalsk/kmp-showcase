package com.mk.kmpshowcase.presentation.screen.storage

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class StorageScreenScreenshotTest(
    stateHolder: StateHolder<StorageUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<StorageUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(StorageScreenPreviewParams())
    }

    @Test
    fun storageScreen() {
        screenshot {
            StorageScreen(state = state)
        }
    }
}
