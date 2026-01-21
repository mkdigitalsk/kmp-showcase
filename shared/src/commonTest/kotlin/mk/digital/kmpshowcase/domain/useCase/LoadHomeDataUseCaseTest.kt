package mk.digital.kmpshowcase.domain.useCase

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import mk.digital.kmpshowcase.domain.BaseTest
import mk.digital.kmpshowcase.domain.model.Address
import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.domain.repository.UserRepository
import mk.digital.kmpshowcase.domain.test
import mk.digital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadHomeDataUseCaseTest : BaseTest<LoadHomeDataUseCase>() {
    override lateinit var classUnderTest: LoadHomeDataUseCase

    private val userRepository: UserRepository = mock()

    override fun beforeEach() {
        classUnderTest = LoadHomeDataUseCase(userRepository)
    }

    @Test
    fun testSuccess() = runTest {
        val users = listOf(
            User(
                address = Address(city = "city", street = "street", suite = "suite", zipcode = "12345"),
                email = "test@test.com",
                id = 1,
                name = "Test User"
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
            }
        )
    }
}
