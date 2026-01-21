package mk.digital.kmpshowcase.data.repository.user

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import mk.digital.kmpshowcase.data.dto.AddressDTO
import mk.digital.kmpshowcase.data.dto.UserDTO
import mk.digital.kmpshowcase.domain.BaseTest
import mk.digital.kmpshowcase.domain.test
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
        val id = 1
        val dto = testUserDTO(id = id)
        val expectedUser = dto.transform()

        test(
            given = {
                everySuspend { client.fetchUser(id) } returns dto
            },
            whenAction = {
                classUnderTest.getUser(id)
            },
            then = {
                assertEquals(expectedUser, it)
            }
        )
    }

    @Test
    fun getUsers() = runTest {
        val dto = testUserDTO()
        val expectedUser = dto.transform()

        test(
            given = {
                everySuspend { client.fetchUsers() } returns listOf(dto)
            },
            whenAction = {
                classUnderTest.getUsers()
            },
            then = {
                assertEquals(listOf(expectedUser), it)
            }
        )
    }
}

// Test Fixtures
private fun testAddressDTO(
    city: String = "Test City",
    street: String = "Test Street",
    suite: String = "Suite 1",
    zipcode: String = "12345"
) = AddressDTO(city = city, street = street, suite = suite, zipcode = zipcode)

private fun testUserDTO(
    id: Int = 1,
    name: String = "Test User",
    email: String = "test@example.com",
    address: AddressDTO = testAddressDTO()
) = UserDTO(address = address, email = email, id = id, name = name)
