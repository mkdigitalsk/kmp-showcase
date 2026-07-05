package com.mk.kmpshowcase.presentation.screen.login

import com.mk.kmpshowcase.domain.model.AuthSession
import com.mk.kmpshowcase.domain.useCase.auth.LoginUseCase
import com.mk.kmpshowcase.domain.useCase.auth.LoginWithTokenUseCase
import com.mk.kmpshowcase.domain.useCase.biometric.AuthenticateWithBiometricUseCase
import com.mk.kmpshowcase.domain.useCase.biometric.IsBiometricEnabledUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModelTest
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginViewModelTest : BaseViewModelTest() {

    private val loginUseCase = mock<LoginUseCase>()
    private val loginWithTokenUseCase = mock<LoginWithTokenUseCase>()
    private val isBiometricEnabledUseCase = mock<IsBiometricEnabledUseCase>()
    private val authenticateWithBiometricUseCase = mock<AuthenticateWithBiometricUseCase>()

    private fun createViewModel(): LoginViewModel {
        everySuspend { loginUseCase(any()) } returns
            AuthSession(token = "token", userId = 1L, email = "test@example.com", name = "Test")
        return LoginViewModel(
            loginUseCase = loginUseCase,
            loginWithTokenUseCase = loginWithTokenUseCase,
            isBiometricEnabledUseCase = isBiometricEnabledUseCase,
            authenticateWithBiometricUseCase = authenticateWithBiometricUseCase,
        )
    }


    @Test
    fun `default state has empty email`() {
        val viewModel = createViewModel()
        assertEquals("", viewModel.state.value.email)
    }

    @Test
    fun `default state has empty password`() {
        val viewModel = createViewModel()
        assertEquals("", viewModel.state.value.password)
    }

    @Test
    fun `default state has no errors`() {
        val viewModel = createViewModel()
        assertNull(viewModel.state.value.emailError)
        assertNull(viewModel.state.value.passwordError)
    }

    @Test
    fun `default state has biometrics unavailable`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.state.value.biometricsAvailable)
    }

    @Test
    fun `default state has biometrics not loading`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.state.value.biometricsLoading)
    }


    @Test
    fun `onEmailChange updates email`() {
        val viewModel = createViewModel()

        viewModel.onEmailChange("test@example.com")

        assertEquals("test@example.com", viewModel.state.value.email)
    }

    @Test
    fun `onEmailChange clears email error`() {
        val viewModel = createViewModel()
        viewModel.login() // Triggers validation error for empty email

        viewModel.onEmailChange("test@example.com")

        assertNull(viewModel.state.value.emailError)
    }


    @Test
    fun `onPasswordChange updates password`() {
        val viewModel = createViewModel()

        viewModel.onPasswordChange("Test123!")

        assertEquals("Test123!", viewModel.state.value.password)
    }

    @Test
    fun `onPasswordChange clears password error`() {
        val viewModel = createViewModel()
        viewModel.login() // Triggers validation error for empty password

        viewModel.onPasswordChange("Test123!")

        assertNull(viewModel.state.value.passwordError)
    }


    @Test
    fun `fillTestAccount sets test email`() {
        val viewModel = createViewModel()

        viewModel.fillTestAccount()

        assertEquals(LoginViewModel.TEST_EMAIL, viewModel.state.value.email)
    }

    @Test
    fun `fillTestAccount sets test password`() {
        val viewModel = createViewModel()

        viewModel.fillTestAccount()

        assertEquals(LoginViewModel.TEST_PASSWORD, viewModel.state.value.password)
    }

    @Test
    fun `fillTestAccount clears errors`() {
        val viewModel = createViewModel()
        viewModel.login() // Triggers validation errors

        viewModel.fillTestAccount()

        assertNull(viewModel.state.value.emailError)
        assertNull(viewModel.state.value.passwordError)
    }


    @Test
    fun `login with empty email shows EMPTY error`() {
        val viewModel = createViewModel()
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertEquals(EmailError.EMPTY, viewModel.state.value.emailError)
    }

    @Test
    fun `login with invalid email format shows INVALID_FORMAT error`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertEquals(EmailError.INVALID_FORMAT, viewModel.state.value.emailError)
    }

    @Test
    fun `login with valid email clears email error`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertNull(viewModel.state.value.emailError)
    }

    @Test
    fun `login with empty password shows EMPTY error`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")

        viewModel.login()

        assertEquals(PasswordError.EMPTY, viewModel.state.value.passwordError)
    }

    @Test
    fun `login with short password shows TOO_SHORT error`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("Test1!")

        viewModel.login()

        assertEquals(PasswordError.TOO_SHORT, viewModel.state.value.passwordError)
    }

    @Test
    fun `login with weak password shows WEAK error`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("testtest") // No uppercase, digit, or special char

        viewModel.login()

        assertEquals(PasswordError.WEAK, viewModel.state.value.passwordError)
    }

    @Test
    fun `login with valid credentials clears all errors`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertNull(viewModel.state.value.emailError)
        assertNull(viewModel.state.value.passwordError)
    }


    @Test
    fun `email without at symbol is invalid`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("testexample.com")
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertEquals(EmailError.INVALID_FORMAT, viewModel.state.value.emailError)
    }

    @Test
    fun `email without domain is invalid`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@")
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertEquals(EmailError.INVALID_FORMAT, viewModel.state.value.emailError)
    }

    @Test
    fun `email with valid format is accepted`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("user.name+tag@example.co.uk")
        viewModel.onPasswordChange("Test123!")

        viewModel.login()

        assertNull(viewModel.state.value.emailError)
    }


    @Test
    fun `password without uppercase is weak`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("test123!")

        viewModel.login()

        assertEquals(PasswordError.WEAK, viewModel.state.value.passwordError)
    }

    @Test
    fun `password without lowercase is weak`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("TEST123!")

        viewModel.login()

        assertEquals(PasswordError.WEAK, viewModel.state.value.passwordError)
    }

    @Test
    fun `password without digit is weak`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("TestTest!")

        viewModel.login()

        assertEquals(PasswordError.WEAK, viewModel.state.value.passwordError)
    }

    @Test
    fun `password without special character is weak`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("Test1234")

        viewModel.login()

        assertEquals(PasswordError.WEAK, viewModel.state.value.passwordError)
    }

    @Test
    fun `strong password is accepted`() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("StrongP@ss1")

        viewModel.login()

        assertNull(viewModel.state.value.passwordError)
    }


    @Test
    fun `LoginUiState default values are correct`() {
        val state = LoginUiState()
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertFalse(state.biometricsAvailable)
        assertFalse(state.biometricsLoading)
    }


    @Test
    fun `EmailError has EMPTY value`() {
        assertEquals(EmailError.EMPTY, EmailError.valueOf("EMPTY"))
    }

    @Test
    fun `EmailError has INVALID_FORMAT value`() {
        assertEquals(EmailError.INVALID_FORMAT, EmailError.valueOf("INVALID_FORMAT"))
    }


    @Test
    fun `PasswordError has EMPTY value`() {
        assertEquals(PasswordError.EMPTY, PasswordError.valueOf("EMPTY"))
    }

    @Test
    fun `PasswordError has TOO_SHORT value`() {
        assertEquals(PasswordError.TOO_SHORT, PasswordError.valueOf("TOO_SHORT"))
    }

    @Test
    fun `PasswordError has WEAK value`() {
        assertEquals(PasswordError.WEAK, PasswordError.valueOf("WEAK"))
    }


    @Test
    fun `TEST_EMAIL is valid email format`() {
        assertTrue(LoginViewModel.TEST_EMAIL.contains("@"))
        assertTrue(LoginViewModel.TEST_EMAIL.contains("."))
    }

    @Test
    fun `TEST_PASSWORD meets all requirements`() {
        val password = LoginViewModel.TEST_PASSWORD
        assertTrue(password.length >= 8)
        assertTrue(password.any { it.isUpperCase() })
        assertTrue(password.any { it.isLowerCase() })
        assertTrue(password.any { it.isDigit() })
        assertTrue(password.any { !it.isLetterOrDigit() })
    }
}
