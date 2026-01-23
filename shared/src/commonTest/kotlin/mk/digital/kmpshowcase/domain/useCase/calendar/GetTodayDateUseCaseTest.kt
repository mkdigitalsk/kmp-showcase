package mk.digital.kmpshowcase.domain.useCase.calendar

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import mk.digital.kmpshowcase.domain.BaseTest
import mk.digital.kmpshowcase.domain.repository.DateRepository
import mk.digital.kmpshowcase.domain.test
import mk.digital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTodayDateUseCaseTest : BaseTest<GetTodayDateUseCase>() {

    override lateinit var classUnderTest: GetTodayDateUseCase

    private val dateRepository = mock<DateRepository>()

    override fun beforeEach() {
        classUnderTest = GetTodayDateUseCase(dateRepository = dateRepository)
    }

    @Test
    fun `returns today date from repository`() = runTest {
        val expectedDate = LocalDate(2024, 6, 15)
        test(
            given = {
                every { dateRepository.today() } returns expectedDate
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                assertEquals(expectedDate, it)
            }
        )
    }

    @Test
    fun `returns different date when repository changes`() = runTest {
        val date1 = LocalDate(2024, 1, 1)
        val date2 = LocalDate(2024, 12, 31)

        every { dateRepository.today() } returns date1
        assertEquals(date1, classUnderTest())

        every { dateRepository.today() } returns date2
        assertEquals(date2, classUnderTest())
    }

    @Test
    fun `handles leap year date`() = runTest {
        val leapYearDate = LocalDate(2024, 2, 29)
        every { dateRepository.today() } returns leapYearDate

        val result = classUnderTest()

        assertEquals(leapYearDate, result)
    }

    @Test
    fun `handles year boundary`() = runTest {
        val newYearsEve = LocalDate(2024, 12, 31)
        every { dateRepository.today() } returns newYearsEve

        val result = classUnderTest()

        assertEquals(2024, result.year)
        assertEquals(12, result.month.number)
        assertEquals(31, result.day)
    }
}
