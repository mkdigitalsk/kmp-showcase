package com.mk.kmpshowcase.presentation.component.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import com.mk.kmpshowcase.domain.model.calendar.DateRange
import com.mk.kmpshowcase.domain.model.calendar.DaySelectionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarUtilsTest {

    @Test
    fun `generateMonth returns 42 days for consistent height`() {
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
        )

        assertEquals(42, result.days.size)
    }

    @Test
    fun `generateMonth correctly identifies current month days`() {
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
        )

        val currentMonthDays = result.days.filter { it.isCurrentMonth }
        assertEquals(31, currentMonthDays.size)
    }

    @Test
    fun `generateMonth correctly identifies today`() {
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
        )

        val todayDay = result.days.find { it.isToday }
        assertEquals(today, todayDay?.date)
    }

    @Test
    fun `generateMonth correctly identifies weekends`() {
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
        )

        val weekends = result.days.filter { it.isWeekend }
        weekends.forEach { day ->
            assertTrue(
                day.date.dayOfWeek == DayOfWeek.SATURDAY ||
                        day.date.dayOfWeek == DayOfWeek.SUNDAY,
            )
        }
    }

    @Test
    fun `generateMonth marks disabled dates correctly`() {
        val disabledDate = LocalDate(2024, 1, 10)
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
            disabledDates = setOf(disabledDate),
        )

        val disabled = result.days.find { it.date == disabledDate }
        assertTrue(disabled?.isDisabled == true)
    }

    @Test
    fun `generateMonth marks dates before minDate as disabled`() {
        val minDate = LocalDate(2024, 1, 15)
        val today = LocalDate(2024, 1, 20)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
            minDate = minDate,
        )

        val beforeMinDate = result.days.filter { it.date < minDate && it.isCurrentMonth }
        beforeMinDate.forEach { day ->
            assertTrue(day.isDisabled, "Date ${day.date} should be disabled")
        }
    }

    @Test
    fun `generateMonth marks dates after maxDate as disabled`() {
        val maxDate = LocalDate(2024, 1, 20)
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
            maxDate = maxDate,
        )

        val afterMaxDate = result.days.filter { it.date > maxDate && it.isCurrentMonth }
        afterMaxDate.forEach { day ->
            assertTrue(day.isDisabled, "Date ${day.date} should be disabled")
        }
    }

    @Test
    fun `getDaySelectionType returns NONE for empty range`() {
        val result = CalendarUtils.getDaySelectionType(
            date = LocalDate(2024, 1, 15),
            selectedRange = DateRange(),
        )

        assertEquals(DaySelectionType.NONE, result)
    }

    @Test
    fun `getDaySelectionType returns SINGLE when only start date is selected`() {
        val date = LocalDate(2024, 1, 15)
        val result = CalendarUtils.getDaySelectionType(
            date = date,
            selectedRange = DateRange(startDate = date),
        )

        assertEquals(DaySelectionType.SINGLE, result)
    }

    @Test
    fun `getDaySelectionType returns SINGLE when start and end are same date`() {
        val date = LocalDate(2024, 1, 15)
        val result = CalendarUtils.getDaySelectionType(
            date = date,
            selectedRange = DateRange(startDate = date, endDate = date),
        )

        assertEquals(DaySelectionType.SINGLE, result)
    }

    @Test
    fun `getDaySelectionType returns START for start date of range`() {
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)
        val result = CalendarUtils.getDaySelectionType(
            date = startDate,
            selectedRange = DateRange(startDate = startDate, endDate = endDate),
        )

        assertEquals(DaySelectionType.START, result)
    }

    @Test
    fun `getDaySelectionType returns END for end date of range`() {
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)
        val result = CalendarUtils.getDaySelectionType(
            date = endDate,
            selectedRange = DateRange(startDate = startDate, endDate = endDate),
        )

        assertEquals(DaySelectionType.END, result)
    }

    @Test
    fun `getDaySelectionType returns IN_RANGE for dates between start and end`() {
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)
        val middleDate = LocalDate(2024, 1, 17)
        val result = CalendarUtils.getDaySelectionType(
            date = middleDate,
            selectedRange = DateRange(startDate = startDate, endDate = endDate),
        )

        assertEquals(DaySelectionType.IN_RANGE, result)
    }

    @Test
    fun `getDaySelectionType returns NONE for dates outside range`() {
        val startDate = LocalDate(2024, 1, 15)
        val endDate = LocalDate(2024, 1, 20)
        val outsideDate = LocalDate(2024, 1, 25)
        val result = CalendarUtils.getDaySelectionType(
            date = outsideDate,
            selectedRange = DateRange(startDate = startDate, endDate = endDate),
        )

        assertEquals(DaySelectionType.NONE, result)
    }

    @Test
    fun `getPreviousMonth returns December of previous year when current is January`() {
        val result = CalendarUtils.getPreviousMonth(2024, 1)
        assertEquals(Pair(2023, 12), result)
    }

    @Test
    fun `getPreviousMonth returns previous month of same year`() {
        val result = CalendarUtils.getPreviousMonth(2024, 6)
        assertEquals(Pair(2024, 5), result)
    }

    @Test
    fun `getNextMonth returns January of next year when current is December`() {
        val result = CalendarUtils.getNextMonth(2024, 12)
        assertEquals(Pair(2025, 1), result)
    }

    @Test
    fun `getNextMonth returns next month of same year`() {
        val result = CalendarUtils.getNextMonth(2024, 6)
        assertEquals(Pair(2024, 7), result)
    }

    @Test
    fun `generateMonth starts week on Monday`() {
        val today = LocalDate(2024, 1, 15)
        val result = CalendarUtils.generateMonth(
            year = 2024,
            month = 1,
            today = today,
        )

        // First day in the grid should be Monday
        assertEquals(DayOfWeek.MONDAY, result.days.first().date.dayOfWeek)
    }
}
