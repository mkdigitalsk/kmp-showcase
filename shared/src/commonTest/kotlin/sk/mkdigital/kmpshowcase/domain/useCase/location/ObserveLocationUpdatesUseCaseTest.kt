package sk.mkdigital.kmpshowcase.domain.useCase.location

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.model.Location
import sk.mkdigital.kmpshowcase.domain.repository.LocationRepository
import sk.mkdigital.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveLocationUpdatesUseCaseTest : BaseTest<ObserveLocationUpdatesUseCase>() {
    override lateinit var classUnderTest: ObserveLocationUpdatesUseCase

    private val locationRepository: LocationRepository = mock()

    override fun beforeEach() {
        classUnderTest = ObserveLocationUpdatesUseCase(locationRepository)
    }

    @Test
    fun `invoke returns flow of locations with high accuracy`() = runTest {
        val location = Location(lat = 48.1486, lon = 17.1077)
        test(
            given = {

                every { locationRepository.locationUpdates(highAccuracy = true) } returns flowOf(location)
            },
            whenAction = {
                classUnderTest(ObserveLocationUpdatesUseCase.Params(highAccuracy = true)).first()
            },
            then = {
                assertEquals(location, it)
            }
        )
    }

    @Test
    fun `invoke returns flow of locations with low accuracy by default`() = runTest {
        val location = Location(lat = 48.1486, lon = 17.1077)
        test(
            given = {
                every { locationRepository.locationUpdates(highAccuracy = false) } returns flowOf(location)
            },
            whenAction = {
                classUnderTest(ObserveLocationUpdatesUseCase.Params()).first()
            },
            then = {
                assertEquals(location, it)
            }
        )
    }
}
