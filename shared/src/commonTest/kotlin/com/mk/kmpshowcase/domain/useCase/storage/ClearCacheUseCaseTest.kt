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
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ClearCacheUseCaseTest : BaseTest<ClearCacheUseCase>() {
    override lateinit var classUnderTest: ClearCacheUseCase

    private val storageRepository: StorageRepository = mock()

    override fun beforeEach() {
        classUnderTest = ClearCacheUseCase(storageRepository)
    }

    @Test
    fun `invoke clears storage repository`() = runTest {
        test(
            given = {
                everySuspend { storageRepository.clear() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { storageRepository.clear() }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Clear failed")

        everySuspend { storageRepository.clear() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
