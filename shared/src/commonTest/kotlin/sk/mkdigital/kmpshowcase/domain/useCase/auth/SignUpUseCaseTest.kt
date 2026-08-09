package sk.mkdigital.kmpshowcase.domain.useCase.auth

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignUpUseCaseTest : BaseTest<SignUpUseCase>() {
    override lateinit var classUnderTest: SignUpUseCase

    private val authRepository: AuthRepository = mock()

    override fun beforeEach() {
        classUnderTest = SignUpUseCase(authRepository)
    }

    @Test
    fun `invoke signs up user and returns auth session`() = runTest {
        val email = "john@example.com"
        val password = "Test123!"
        val expectedSession = AuthSession(
            token = "jwt-token",
            userId = 1L,
            email = email,
        )

        test(
            given = {
                everySuspend { authRepository.signUp(email, password) } returns expectedSession
            },
            whenAction = {
                classUnderTest(SignUpUseCase.Params(email, password))
            },
            then = { result ->
                assertEquals(expectedSession, result)
                verifySuspend { authRepository.signUp(email, password) }
            }
        )
    }

    @Test
    fun `invoke throws exception when sign up fails`() = runTest {
        val email = "john@example.com"
        val password = "Test123!"
        val exception = RuntimeException("Sign up failed")

        everySuspend { authRepository.signUp(email, password) } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest(SignUpUseCase.Params(email, password))
        }
    }
}
