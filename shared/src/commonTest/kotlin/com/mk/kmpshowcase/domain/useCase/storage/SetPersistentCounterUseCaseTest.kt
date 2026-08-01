package com.mk.kmpshowcase.domain.useCase.storage

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.StorageRepository
import com.mk.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SetPersistentCounterUseCaseTest : BaseTest<SetPersistentCounterUseCase>() {
    override lateinit var classUnderTest: SetPersistentCounterUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = SetPersistentCounterUseCase(storageRepository)
    }

    @Test
    fun `invoke sets persistent counter in repository`() = runTest {
        val counter = 15

        test(
            given = {
                everySuspend { storageRepository.setPersistentCounter(counter) } returns Unit
            },
            whenAction = {
                classUnderTest(counter)
            },
            then = {
                verifySuspend { storageRepository.setPersistentCounter(counter) }
            }
        )
    }

    @Test
    fun `invoke handles zero counter`() = runTest {
        test(
            given = {
                everySuspend { storageRepository.setPersistentCounter(0) } returns Unit
            },
            whenAction = {
                classUnderTest(0)
            },
            then = {
                verifySuspend { storageRepository.setPersistentCounter(0) }
            }
        )
    }

    @Test
    fun `invoke handles negative counter`() = runTest {
        val counter = -5

        test(
            given = {
                everySuspend { storageRepository.setPersistentCounter(counter) } returns Unit
            },
            whenAction = {
                classUnderTest(counter)
            },
            then = {
                verifySuspend { storageRepository.setPersistentCounter(counter) }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Storage error")

        everySuspend { storageRepository.setPersistentCounter(10) } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest(10)
        }
    }
}
