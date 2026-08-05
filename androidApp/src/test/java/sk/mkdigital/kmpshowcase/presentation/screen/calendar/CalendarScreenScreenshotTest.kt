package sk.mkdigital.kmpshowcase.presentation.screen.calendar

import sk.mkdigital.kmpshowcase.presentation.base.BaseScreenshotTest
import sk.mkdigital.kmpshowcase.presentation.base.ScreenshotMode
import sk.mkdigital.kmpshowcase.presentation.base.StateHolder
import sk.mkdigital.kmpshowcase.presentation.base.generateParameterizedData
import org.junit.Test
import org.robolectric.ParameterizedRobolectricTestRunner

class CalendarScreenScreenshotTest(
    stateHolder: StateHolder<CalendarUiState>,
    mode: ScreenshotMode,
) : BaseScreenshotTest<CalendarUiState>(stateHolder, mode) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<*> = generateParameterizedData(CalendarScreenPreviewParams())
    }

    @Test
    fun calendarScreen() {
        screenshot {
            CalendarScreen(state = state)
        }
    }
}
