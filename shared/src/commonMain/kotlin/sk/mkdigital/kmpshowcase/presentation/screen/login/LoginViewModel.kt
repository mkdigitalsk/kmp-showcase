package sk.mkdigital.kmpshowcase.presentation.screen.login

import sk.mkdigital.kmpshowcase.domain.model.BiometricResult
import sk.mkdigital.kmpshowcase.domain.useCase.auth.LoginUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.auth.LoginWithTokenUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import sk.mkdigital.kmpshowcase.domain.useCase.biometric.AuthenticateWithBiometricUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.biometric.IsBiometricEnabledUseCase
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel
import sk.mkdigital.kmpshowcase.presentation.base.NavEvent
import sk.mkdigital.kmpshowcase.presentation.util.ValidationPatterns

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithTokenUseCase: LoginWithTokenUseCase,
    private val isBiometricEnabledUseCase: IsBiometricEnabledUseCase,
    private val authenticateWithBiometricUseCase: AuthenticateWithBiometricUseCase,
) : BaseViewModel<LoginUiState>(LoginUiState()) {


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
            onLoading = { newState { it.copy(biometricsLoading = true) } },
            onSuccess = { result ->
                newState { it.copy(biometricsLoading = false) }
                if (result is BiometricResult.Success) navigate(LoginNavEvent.ToHome)
            },
            onError = { newState { it.copy(biometricsLoading = false) } }
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
        const val TEST_EMAIL = "test01@mkdigital.sk"
        const val TEST_PASSWORD = "MKDigitalTest1@"
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
)

sealed interface LoginNavEvent : NavEvent {
    data object ToHome : LoginNavEvent
    data object ToRegister : LoginNavEvent
}
