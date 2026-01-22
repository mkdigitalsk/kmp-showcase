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
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SetSessionCounterUseCaseTest : BaseTest<SetSessionCounterUseCase>() {
    override lateinit var classUnderTest: SetSessionCounterUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = SetSessionCounterUseCase(storageRepository)
    }

    @Test
    fun `invoke sets session counter in repository`() = runTest {
        val counter = 5

        test(
            given = {
                everySuspend { storageRepository.setSessionCounter(counter) } returns Unit
            },
            whenAction = {
                classUnderTest(counter)
            },
            then = {
                verifySuspend { storageRepository.setSessionCounter(counter) }
            }
        )
    }

    @Test
    fun `invoke handles zero counter`() = runTest {
        test(
            given = {
                everySuspend { storageRepository.setSessionCounter(0) } returns Unit
            },
            whenAction = {
                classUnderTest(0)
            },
            then = {
                verifySuspend { storageRepository.setSessionCounter(0) }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Storage error")

        everySuspend { storageRepository.setSessionCounter(10) } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest(10)
        }
    }
}
