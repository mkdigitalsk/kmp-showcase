package mk.digital.kmpshowcase.domain.useCase.storage

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mk.digital.kmpshowcase.domain.BaseTest
import mk.digital.kmpshowcase.domain.model.StorageData
import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.test
import mk.digital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveStorageDataUseCaseTest : BaseTest<ObserveStorageDataUseCase>() {
    override lateinit var classUnderTest: ObserveStorageDataUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = ObserveStorageDataUseCase(storageRepository)
    }

    @Test
    fun `invoke returns storage data flow from repository`() = runTest {
        val storageData = StorageData(
            sessionValue = "session",
            persistentValue = "persistent"
        )

        test(
            given = {
                every { storageRepository.storageData } returns flowOf(storageData)
            },
            whenAction = {
                classUnderTest().first()
            },
            then = {
                assertEquals(storageData, it)
                assertEquals("session", it.sessionValue)
                assertEquals("persistent", it.persistentValue)
            }
        )
    }

    @Test
    fun `invoke returns default storage data when empty`() = runTest {
        val defaultData = StorageData()

        test(
            given = {
                every { storageRepository.storageData } returns flowOf(defaultData)
            },
            whenAction = {
                classUnderTest().first()
            },
            then = {
                assertEquals("", it.sessionValue)
                assertEquals("", it.persistentValue)
            }
        )
    }
}
