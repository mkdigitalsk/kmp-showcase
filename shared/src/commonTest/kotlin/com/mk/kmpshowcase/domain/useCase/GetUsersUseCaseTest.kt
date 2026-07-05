package com.mk.kmpshowcase.domain.useCase

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.repository.UserRepository
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GetUsersUseCaseTest : BaseTest<GetUsersUseCase>() {
    override lateinit var classUnderTest: GetUsersUseCase

    private val userRepository: UserRepository = mock()

    override fun beforeEach() {
        classUnderTest = GetUsersUseCase(userRepository)
    }

    @Test
    fun `invoke returns list of users from repository`() = runTest {
        val users = listOf(
            User(
                email = "john@example.com",
                id = 1L,
                name = "John Doe"
            ),
            User(
                email = "jane@example.com",
                id = 2L,
                name = "Jane Smith"
            )
        )

        test(
            given = {
                everySuspend { userRepository.getUsers() } returns users
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                assertEquals(users, it)
                assertEquals(2, it.size)
                assertEquals("John Doe", it[0].name)
                assertEquals("Jane Smith", it[1].name)
            }
        )
    }

    @Test
    fun `invoke returns empty list when no users`() = runTest {
        test(
            given = {
                everySuspend { userRepository.getUsers() } returns emptyList()
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                assertTrue(it.isEmpty())
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Network error")

        everySuspend { userRepository.getUsers() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
