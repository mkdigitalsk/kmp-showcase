package com.mk.kmpshowcase.presentation.screen.login

import com.mk.kmpshowcase.domain.model.BiometricResult
import com.mk.kmpshowcase.domain.useCase.auth.LoginUseCase
import com.mk.kmpshowcase.domain.useCase.auth.LoginWithTokenUseCase
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.invoke
import com.mk.kmpshowcase.domain.useCase.biometric.AuthenticateWithBiometricUseCase
import com.mk.kmpshowcase.domain.useCase.biometric.IsBiometricEnabledUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModel
import com.mk.kmpshowcase.presentation.base.NavEvent
import com.mk.kmpshowcase.presentation.util.ValidationPatterns

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithTokenUseCase: LoginWithTokenUseCase,
    private val isBiometricEnabledUseCase: IsBiometricEnabledUseCase,
    private val authenticateWithBiometricUseCase: AuthenticateWithBiometricUseCase,
) : BaseViewModel<LoginUiState>(LoginUiState()) {

    fun skip() = navigate(LoginNavEvent.ToHome)

    fun toRegister() = navigate(LoginNavEvent.ToRegister)

    override fun loadInitialData() {
        execute(
            action = { loginWithTokenUseCase() },
            onLoading = { newState { it.copy(isLoading = true) } },
            onSuccess = { session ->
                newState { it.copy(isLoading = false) }
                if (session != null) navigate(LoginNavEvent.ToHome)
            },
            onError = { newState { it.copy(isLoading = false) } }
        )
        execute(
            action = { isBiometricEnabledUseCase() },
            onSuccess = { enabled -> newState { it.copy(biometricsAvailable = enabled) } }
        )
    }

    fun onEmailChange(email: String) = newState { it.copy(email = email, emailError = null) }

    fun onPasswordChange(password: String) = newState { it.copy(password = password, passwordError = null) }

    fun fillTestAccount() {
        newState {
            it.copy(
                email = TEST_EMAIL,
                password = TEST_PASSWORD,
                emailError = null,
                passwordError = null
            )
        }
    }

    fun login() {
        requireState { state ->
            val emailError = validateEmail(state.email)
            val passwordError = validatePassword(state.password)

            if (emailError != null || passwordError != null) {
                newState { it.copy(emailError = emailError, passwordError = passwordError) }
                return@requireState
            }

            execute(
                action = { loginUseCase(LoginUseCase.Params(state.email, state.password)) },
                onLoading = { newState { it.copy(isLoading = true) } },
                onSuccess = {
                    newState { it.copy(isLoading = false) }
                    navigate(LoginNavEvent.ToHome)
                },
                onError = { error ->
                    newState { it.copy(isLoading = false, serverError = error.message) }
                }
            )
        }
    }

    fun authenticateWithBiometrics() {
        execute(
            action = { authenticateWithBiometricUseCase() },
            onLoading = { newState { it.copy(biometricsLoading = true, biometricsResult = null) } },
            onSuccess = { result ->
                newState { it.copy(biometricsLoading = false, biometricsResult = result) }
                if (result is BiometricResult.Success) navigate(LoginNavEvent.ToHome)
            },
            onError = { error ->
                newState {
                    it.copy(
                        biometricsLoading = false,
                        biometricsResult = BiometricResult.SystemError(error.message.orEmpty())
                    )
                }
            }
        )
    }

    private fun validateEmail(email: String): EmailError? = when {
        email.isBlank() -> EmailError.EMPTY
        !ValidationPatterns.isValidEmail(email) -> EmailError.INVALID_FORMAT
        else -> null
    }

    private fun validatePassword(password: String): PasswordError? = when {
        password.isBlank() -> PasswordError.EMPTY
        !ValidationPatterns.isPasswordLongEnough(password) -> PasswordError.TOO_SHORT
        !ValidationPatterns.isValidPassword(password) -> PasswordError.WEAK
        else -> null
    }

    companion object {
        const val TEST_EMAIL = "test01@test.com"
        const val TEST_PASSWORD = "Kmpshowcase1@"
    }
}

enum class EmailError { EMPTY, INVALID_FORMAT }
enum class PasswordError { EMPTY, TOO_SHORT, WEAK }

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: EmailError? = null,
    val passwordError: PasswordError? = null,
    val isLoading: Boolean = false,
    val serverError: String? = null,
    val biometricsAvailable: Boolean = false,
    val biometricsLoading: Boolean = false,
    val biometricsResult: BiometricResult? = null,
)

sealed interface LoginNavEvent : NavEvent {
    data object ToHome : LoginNavEvent
    data object ToRegister : LoginNavEvent
}
