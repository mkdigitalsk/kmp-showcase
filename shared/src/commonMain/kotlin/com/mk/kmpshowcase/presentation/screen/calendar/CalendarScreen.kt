package com.mk.kmpshowcase.presentation.screen.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.buttons.OutlinedButton
import com.mk.kmpshowcase.presentation.component.calendar.CalendarView
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import com.mk.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargeNeutral80
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.space16
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.domain.model.calendar.DateRange
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.calendar_clear_selection
import com.mk.kmpshowcase.shared.generated.resources.calendar_no_dates_selected
import com.mk.kmpshowcase.shared.generated.resources.calendar_range_format
import com.mk.kmpshowcase.shared.generated.resources.calendar_selected_range
import com.mk.kmpshowcase.shared.generated.resources.calendar_single_date
import com.mk.kmpshowcase.shared.generated.resources.calendar_start_date
import com.mk.kmpshowcase.shared.generated.resources.calendar_subtitle
import com.mk.kmpshowcase.shared.generated.resources.calendar_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = lifecycleAwareViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalendarScreen(
        state = state,
        onDateClick = viewModel::onDateClick,
        onClearSelection = viewModel::clearSelection,
    )
}

@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onDateClick: (kotlinx.datetime.LocalDate) -> Unit = {},
    onClearSelection: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space4,
            end = space4,
            top = space4,
            bottom = floatingNavBarSpace + space16,
        ),
        verticalArrangement = Arrangement.spacedBy(space4),
    ) {
        item {
            Column {
                TextHeadlineMediumPrimary(stringResource(Res.string.calendar_title))
                TextBodyMediumNeutral80(stringResource(Res.string.calendar_subtitle))
            }
        }

        state.today?.let { today ->
            item {
                AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
                    CalendarView(
                        selectedRange = state.selectedRange,
                        onDateClick = onDateClick,
                        today = today,
                        disabledDates = state.disabledDates,
                        minDate = state.minDate,
                        maxDate = state.maxDate,
                    )
                }
            }
        }

        item {
            AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
                TextTitleLargeNeutral80(stringResource(Res.string.calendar_selected_range))
                Spacer2()
                val rangeText = buildRangeText(state.selectedRange)
                TextBodyMediumNeutral80(rangeText)
            }
        }

        item {
            OutlinedButton(
                text = stringResource(Res.string.calendar_clear_selection),
                onClick = onClearSelection,
            )
        }
    }
}

@Composable
private fun buildRangeText(range: DateRange): String {
    val start = range.startDate
    val end = range.endDate

    return when {
        start == null -> stringResource(Res.string.calendar_no_dates_selected)
        end == null -> stringResource(Res.string.calendar_start_date, start.toString())
        start == end -> stringResource(Res.string.calendar_single_date, start.toString())
        else -> stringResource(Res.string.calendar_range_format, start.toString(), end.toString())
    }
}
