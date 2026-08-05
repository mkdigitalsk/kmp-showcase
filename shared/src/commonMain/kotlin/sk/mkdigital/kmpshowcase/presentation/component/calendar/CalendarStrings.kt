package sk.mkdigital.kmpshowcase.presentation.component.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_april
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_august
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_december
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_february
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_january
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_july
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_june
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_march
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_may
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_november
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_october
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_month_september
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_fri
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_mon
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_sat
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_sun
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_thu
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_tue
import sk.mkdigital.kmpshowcase.shared.generated.resources.calendar_weekday_wed
import org.jetbrains.compose.resources.stringResource

private const val MONTH_TO_INDEX_OFFSET = 1

@Immutable
data class CalendarStrings(
    val weekDayLabels: List<String>,
    val monthNames: List<String>,
) {
    fun getMonthName(month: Int): String = monthNames.getOrElse(month - MONTH_TO_INDEX_OFFSET) { "" }

    companion object {
        @Composable
        fun default(): CalendarStrings = CalendarStrings(
            weekDayLabels = listOf(
                stringResource(Res.string.calendar_weekday_mon),
                stringResource(Res.string.calendar_weekday_tue),
                stringResource(Res.string.calendar_weekday_wed),
                stringResource(Res.string.calendar_weekday_thu),
                stringResource(Res.string.calendar_weekday_fri),
                stringResource(Res.string.calendar_weekday_sat),
                stringResource(Res.string.calendar_weekday_sun),
            ),
            monthNames = listOf(
                stringResource(Res.string.calendar_month_january),
                stringResource(Res.string.calendar_month_february),
                stringResource(Res.string.calendar_month_march),
                stringResource(Res.string.calendar_month_april),
                stringResource(Res.string.calendar_month_may),
                stringResource(Res.string.calendar_month_june),
                stringResource(Res.string.calendar_month_july),
                stringResource(Res.string.calendar_month_august),
                stringResource(Res.string.calendar_month_september),
                stringResource(Res.string.calendar_month_october),
                stringResource(Res.string.calendar_month_november),
                stringResource(Res.string.calendar_month_december),
            ),
        )
    }
}
