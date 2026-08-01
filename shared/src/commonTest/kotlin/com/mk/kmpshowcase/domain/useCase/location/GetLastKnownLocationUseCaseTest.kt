package com.mk.kmpshowcase.domain.useCase.location

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.Location
import com.mk.kmpshowcase.domain.repository.LocationRepository
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLastKnownLocationUseCaseTest : BaseTest<GetLastKnownLocationUseCase>() {
    override lateinit var classUnderTest: GetLastKnownLocationUseCase

    private val locationRepository: LocationRepository = mock()

    override fun beforeEach() {
        classUnderTest = GetLastKnownLocationUseCase(locationRepository)
    }

    @Test
    fun testSuccess() = runTest {
        val expectedLocation = Location(lat = 48.1486, lon = 17.1077)

        test(
            given = {
                everySuspend { locationRepository.lastKnownLocation() } returns expectedLocation
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(expectedLocation, result)
                verifySuspend { locationRepository.lastKnownLocation() }
            }
        )
    }
}
