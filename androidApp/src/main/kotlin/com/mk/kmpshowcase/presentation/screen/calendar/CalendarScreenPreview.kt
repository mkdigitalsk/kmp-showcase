package com.mk.kmpshowcase.presentation.screen.calendar

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.datetime.LocalDate
import com.mk.kmpshowcase.domain.model.calendar.DateRange
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CalendarScreenPreview(
    @PreviewParameter(CalendarScreenPreviewParams::class) state: CalendarUiState
) {
    AppTheme {
        CalendarScreen(state = state)
    }
}

internal class CalendarScreenPreviewParams : PreviewParameterProvider<CalendarUiState> {
    private val year = 2025
    private val month = 1
    private val today = LocalDate(year, month, 15)

    override val values = sequenceOf(
        CalendarUiState(today = today),
        CalendarUiState(
            today = today,
            selectedRange = DateRange(
                startDate = LocalDate(year, month, 10),
                endDate = LocalDate(year, month, 20)
            ),
            disabledDates = setOf(
                LocalDate(year, month, 7),
                LocalDate(year, month, 21)
            )
        ),
    )
}
