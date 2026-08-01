package com.mk.kmpshowcase.presentation.screen.calendar

import com.mk.kmpshowcase.presentation.base.BaseScreenshotTest
import com.mk.kmpshowcase.presentation.base.ScreenshotMode
import com.mk.kmpshowcase.presentation.base.StateHolder
import com.mk.kmpshowcase.presentation.base.generateParameterizedData
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
