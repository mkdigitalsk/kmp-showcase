package com.mk.kmpshowcase.data.repository.user

import com.mk.kmpshowcase.contracts.user.ThemeModeDTO
import com.mk.kmpshowcase.contracts.user.UserResponseDTO
import com.mk.kmpshowcase.data.client.UserClient
import com.mk.kmpshowcase.data.repository.UserRepositoryImpl
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.test
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRepositoryImplTest : BaseTest<UserRepositoryImpl>() {
    override lateinit var classUnderTest: UserRepositoryImpl

    private val client: UserClient = mock()

    override fun beforeEach() {
        classUnderTest = UserRepositoryImpl(client)
    }

    @Test
    fun getUser() = runTest {
        val dto = testUserResponseDTO(id = 1L)
        val expected = User(id = dto.id, email = dto.email, name = dto.name)

        test(
            given = { everySuspend { client.fetchUser(1L) } returns dto },
            whenAction = { classUnderTest.getUser(1L) },
            then = { assertEquals(expected, it) }
        )
    }

    @Test
    fun getUsers() = runTest {
        val dto = testUserResponseDTO()
        val expected = User(id = dto.id, email = dto.email, name = dto.name)

        test(
            given = { everySuspend { client.fetchUsers() } returns listOf(dto) },
            whenAction = { classUnderTest.getUsers() },
            then = { assertEquals(listOf(expected), it) }
        )
    }
}

private fun testUserResponseDTO(
    id: Long = 1L,
    name: String = "Test User",
    email: String = "test@example.com",
    createdAt: Long = 0L,
    themeMode: ThemeModeDTO = ThemeModeDTO.SYSTEM,
    locale: String = "en-GB",
) = UserResponseDTO(
    id = id,
    email = email,
    name = name,
    createdAt = createdAt,
    themeMode = themeMode,
    locale = locale,
)
