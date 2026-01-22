package mk.digital.kmpshowcase.domain.useCase.storage

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import mk.digital.kmpshowcase.domain.BaseTest
import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.test
import mk.digital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LoadStorageDataUseCaseTest : BaseTest<LoadStorageDataUseCase>() {
    override lateinit var classUnderTest: LoadStorageDataUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = LoadStorageDataUseCase(storageRepository)
    }

    @Test
    fun `invoke calls repository loadInitialData`() = runTest {
        test(
            given = {
                everySuspend { storageRepository.loadInitialData() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { storageRepository.loadInitialData() }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Storage error")

        everySuspend { storageRepository.loadInitialData() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
