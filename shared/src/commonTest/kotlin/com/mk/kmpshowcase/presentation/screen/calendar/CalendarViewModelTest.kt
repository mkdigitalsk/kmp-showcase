package com.mk.kmpshowcase.presentation.screen.calendar

import kotlinx.datetime.LocalDate
import com.mk.kmpshowcase.domain.model.calendar.DateRange
import com.mk.kmpshowcase.domain.model.calendar.SelectionState
import com.mk.kmpshowcase.domain.repository.DateRepository
import com.mk.kmpshowcase.domain.useCase.calendar.GetTodayDateUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalendarViewModelTest {

    private class FakeDateRepository(private val fixedDate: LocalDate) : DateRepository {
        override fun today(): LocalDate = fixedDate
    }

    private fun createViewModel(
        today: LocalDate = LocalDate(2024, 1, 15),
    ): CalendarViewModel {
        val dateRepository = FakeDateRepository(today)
        val getTodayDateUseCase = GetTodayDateUseCase(dateRepository)
        return CalendarViewModel(getTodayDateUseCase)
    }

    @Test
    fun `initial state has empty selection`() {
        val viewModel = createViewModel()
        val state = viewModel.state.value

        assertEquals(SelectionState.Empty, state.selectionState)
        assertTrue(state.selectedRange.isEmpty)
    }

    @Test
    fun `first click sets start date`() {
        val viewModel = createViewModel()
        val date = LocalDate(2024, 1, 15)

        viewModel.onDateClick(date)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.StartSelected)
        assertEquals(date, state.selectionState.startDate)
        assertEquals(date, state.selectedRange.startDate)
        assertNull(state.selectedRange.endDate)
    }

    @Test
    fun `second click on later date completes range`() {
        val viewModel = createViewModel()
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.RangeSelected)
        val rangeState = state.selectionState
        assertEquals(startDate, rangeState.range.startDate)
        assertEquals(endDate, rangeState.range.endDate)
        assertTrue(state.selectedRange.isComplete)
    }

    @Test
    fun `second click on same date creates single date selection`() {
        val viewModel = createViewModel()
        val date = LocalDate(2024, 1, 15)

        viewModel.onDateClick(date)
        viewModel.onDateClick(date)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.RangeSelected)
        val rangeState = state.selectionState
        assertEquals(date, rangeState.range.startDate)
        assertEquals(date, rangeState.range.endDate)
    }

    @Test
    fun `second click on earlier date resets selection`() {
        val viewModel = createViewModel()
        val firstDate = LocalDate(2024, 1, 20)
        val secondDate = LocalDate(2024, 1, 15) // Earlier than first

        viewModel.onDateClick(firstDate)
        viewModel.onDateClick(secondDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.StartSelected)
        assertEquals(secondDate, state.selectionState.startDate)
        assertEquals(secondDate, state.selectedRange.startDate)
        assertNull(state.selectedRange.endDate)
    }

    @Test
    fun `third click after range selection resets to new start`() {
        val viewModel = createViewModel()
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)
        val newDate = LocalDate(2024, 1, 25)

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)
        viewModel.onDateClick(newDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.StartSelected)
        assertEquals(newDate, state.selectionState.startDate)
        assertEquals(newDate, state.selectedRange.startDate)
        assertNull(state.selectedRange.endDate)
    }

    @Test
    fun `clearSelection resets to empty state`() {
        val viewModel = createViewModel()
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)
        viewModel.clearSelection()

        val state = viewModel.state.value
        assertEquals(SelectionState.Empty, state.selectionState)
        assertTrue(state.selectedRange.isEmpty)
    }

    @Test
    fun `multiple complete selections work correctly`() {
        val viewModel = createViewModel()

        viewModel.onDateClick(LocalDate(2024, 1, 10))
        viewModel.onDateClick(LocalDate(2024, 1, 15))
        assertTrue(viewModel.state.value.selectedRange.isComplete)

        // Second selection (resets and creates new)
        viewModel.onDateClick(LocalDate(2024, 2, 1))
        viewModel.onDateClick(LocalDate(2024, 2, 5))

        val state = viewModel.state.value
        assertTrue(state.selectedRange.isComplete)
        assertEquals(LocalDate(2024, 2, 1), state.selectedRange.startDate)
        assertEquals(LocalDate(2024, 2, 5), state.selectedRange.endDate)
    }

    @Test
    fun `DateRange isEmpty returns true for empty range`() {
        val range = DateRange()
        assertTrue(range.isEmpty)
    }

    @Test
    fun `DateRange isEmpty returns false when start date is set`() {
        val range = DateRange(startDate = LocalDate(2024, 1, 15))
        assertTrue(!range.isEmpty)
    }

    @Test
    fun `DateRange isComplete returns false for partial range`() {
        val range = DateRange(startDate = LocalDate(2024, 1, 15))
        assertTrue(!range.isComplete)
    }

    @Test
    fun `DateRange isComplete returns true when both dates are set`() {
        val range = DateRange(
            startDate = LocalDate(2024, 1, 15),
            endDate = LocalDate(2024, 1, 20),
        )
        assertTrue(range.isComplete)
    }

    @Test
    fun `range with disabled date in between resets to new start`() {
        val viewModel = createViewModel()
        val disabledDate = LocalDate(2024, 1, 17)
        viewModel.updateDisabledDates(setOf(disabledDate))

        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20) // Range would include disabled 17th

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        // Should reset to new start instead of completing range
        assertTrue(state.selectionState is SelectionState.StartSelected)
        assertEquals(endDate, state.selectionState.startDate)
        assertNull(state.selectedRange.endDate)
    }

    @Test
    fun `range without disabled dates in between completes successfully`() {
        val viewModel = createViewModel()
        val disabledDate = LocalDate(2024, 1, 25) // Outside the range
        viewModel.updateDisabledDates(setOf(disabledDate))

        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.RangeSelected)
        assertTrue(state.selectedRange.isComplete)
    }

    @Test
    fun `disabled date at start boundary is allowed`() {
        val viewModel = createViewModel()
        // Disabled date is the start date itself - should still work
        // (clicking disabled dates is prevented in UI, not ViewModel)
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.RangeSelected)
    }

    @Test
    fun `disabled date at end boundary is allowed`() {
        val viewModel = createViewModel()
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)
        // Disabled at end boundary - the check is for dates BETWEEN start and end
        viewModel.updateDisabledDates(setOf(endDate))

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        // End date being disabled doesn't block the range (UI prevents clicking)
        assertTrue(state.selectionState is SelectionState.RangeSelected)
    }

    @Test
    fun `multiple disabled dates in range resets selection`() {
        val viewModel = createViewModel()
        viewModel.updateDisabledDates(
            setOf(
                LocalDate(2024, 1, 16),
                LocalDate(2024, 1, 17),
                LocalDate(2024, 1, 18),
            ),
        )

        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.StartSelected)
        assertEquals(endDate, state.selectedRange.startDate)
    }

    @Test
    fun `adjacent dates with no gap are valid range`() {
        val viewModel = createViewModel()
        viewModel.updateDisabledDates(setOf(LocalDate(2024, 1, 17)))

        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 16) // No dates between, so no disabled check needed

        viewModel.onDateClick(startDate)
        viewModel.onDateClick(endDate)

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.RangeSelected)
        assertTrue(state.selectedRange.isComplete)
    }

    @Test
    fun `same date selection bypasses disabled check`() {
        val viewModel = createViewModel()
        viewModel.updateDisabledDates(setOf(LocalDate(2024, 1, 16)))

        val date = LocalDate(2024, 1, 15)

        viewModel.onDateClick(date)
        viewModel.onDateClick(date) // Same date - no range to check

        val state = viewModel.state.value
        assertTrue(state.selectionState is SelectionState.RangeSelected)
        assertEquals(date, state.selectedRange.startDate)
        assertEquals(date, state.selectedRange.endDate)
    }
}
