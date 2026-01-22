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

class SetPersistentValueUseCaseTest : BaseTest<SetPersistentValueUseCase>() {
    override lateinit var classUnderTest: SetPersistentValueUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = SetPersistentValueUseCase(storageRepository)
    }

    @Test
    fun `invoke sets persistent value in repository`() = runTest {
        val value = "testPersistent"

        test(
            given = {
                everySuspend { storageRepository.setPersistentValue(value) } returns Unit
            },
            whenAction = {
                classUnderTest(value)
            },
            then = {
                verifySuspend { storageRepository.setPersistentValue(value) }
            }
        )
    }

    @Test
    fun `invoke handles empty value`() = runTest {
        val emptyValue = ""

        test(
            given = {
                everySuspend { storageRepository.setPersistentValue(emptyValue) } returns Unit
            },
            whenAction = {
                classUnderTest(emptyValue)
            },
            then = {
                verifySuspend { storageRepository.setPersistentValue(emptyValue) }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Storage error")

        everySuspend { storageRepository.setPersistentValue("test") } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest("test")
        }
    }
}
