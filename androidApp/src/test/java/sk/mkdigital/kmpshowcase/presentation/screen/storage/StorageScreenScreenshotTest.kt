package sk.mkdigital.kmpshowcase.presentation.screen.storage

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
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
