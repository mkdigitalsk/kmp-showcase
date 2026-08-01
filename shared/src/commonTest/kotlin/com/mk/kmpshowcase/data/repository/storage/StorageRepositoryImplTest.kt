package com.mk.kmpshowcase.data.repository.storage

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.data.local.StorageLocalStore
import com.mk.kmpshowcase.data.repository.StorageRepositoryImpl
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.StorageData
import com.mk.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageRepositoryImplTest : BaseTest<StorageRepositoryImpl>() {
    override lateinit var classUnderTest: StorageRepositoryImpl

    private val storageLocalStore: StorageLocalStore = mock()
    private val dataFlow = MutableStateFlow(StorageData())

    override fun beforeEach() {
        everySuspend { storageLocalStore.data } returns dataFlow
        classUnderTest = StorageRepositoryImpl(storageLocalStore)
    }

    @Test
    fun `storageData emits data from local store`() = runTest {
        val expectedData = StorageData(sessionCounter = 5, persistentCounter = 10)
        dataFlow.value = expectedData

        val result = classUnderTest.storageData.first()

        assertEquals(expectedData, result)
    }

    @Test
    fun `loadInitialData calls local store load`() = runTest {
        test(
            given = {
                everySuspend { storageLocalStore.load() } returns Unit
            },
            whenAction = {
                classUnderTest.loadInitialData()
            },
            then = {
                verifySuspend { storageLocalStore.load() }
            }
        )
    }

    @Test
    fun `setSessionCounter calls local store with correct value`() = runTest {
        val counterValue = 42

        test(
            given = {
                everySuspend { storageLocalStore.setSessionCounter(counterValue) } returns Unit
            },
            whenAction = {
                classUnderTest.setSessionCounter(counterValue)
            },
            then = {
                verifySuspend { storageLocalStore.setSessionCounter(counterValue) }
            }
        )
    }

    @Test
    fun `setPersistentCounter calls local store with correct value`() = runTest {
        val counterValue = 99

        test(
            given = {
                everySuspend { storageLocalStore.setPersistentCounter(counterValue) } returns Unit
            },
            whenAction = {
                classUnderTest.setPersistentCounter(counterValue)
            },
            then = {
                verifySuspend { storageLocalStore.setPersistentCounter(counterValue) }
            }
        )
    }

    @Test
    fun `clear calls local store clear`() = runTest {
        test(
            given = {
                everySuspend { storageLocalStore.clear() } returns Unit
            },
            whenAction = {
                classUnderTest.clear()
            },
            then = {
                verifySuspend { storageLocalStore.clear() }
            }
        )
    }
}
