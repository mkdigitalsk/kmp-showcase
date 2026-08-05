package sk.mkdigital.kmpshowcase.presentation.screen.database

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
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
