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

class ClearSessionUseCaseTest : BaseTest<ClearCacheUseCase>() {
    override lateinit var classUnderTest: ClearCacheUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = ClearCacheUseCase(storageRepository)
    }

    @Test
    fun `invoke clears session data in repository`() = runTest {
        test(
            given = {
                everySuspend { storageRepository.clearSession() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { storageRepository.clearSession() }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Storage error")

        everySuspend { storageRepository.clearSession() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
