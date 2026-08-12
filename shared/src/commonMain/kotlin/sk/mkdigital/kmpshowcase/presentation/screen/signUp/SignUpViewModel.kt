package sk.mkdigital.kmpshowcase.presentation.screen.signUp

import sk.mkdigital.kmpshowcase.domain.exceptions.base.BaseException
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignUpUseCase
import sk.mkdigital.kmpshowcase.presentation.base.AppError
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel
import sk.mkdigital.kmpshowcase.presentation.base.toAppError
import sk.mkdigital.kmpshowcase.presentation.base.NavEvent
import sk.mkdigital.kmpshowcase.presentation.util.ValidationPatterns

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
) : BaseViewModel<SignUpUiState>(SignUpUiState()) {

    fun onEmailChange(email: String) {
        newState { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        newState { it.copy(password = password, passwordError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        newState { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun signUp() {
        requireState { state ->
            val emailError = validateEmail(state.email)
            val passwordError = validatePassword(state.password)
            val confirmPasswordError = validateConfirmPassword(state.password, state.confirmPassword)

            if (emailError != null || passwordError != null || confirmPasswordError != null) {
                newState {
                    it.copy(
                        emailError = emailError,
                        passwordError = passwordError,
                        confirmPasswordError = confirmPasswordError
                    )
                }
                return@requireState
            }

            performSignUp(state.email, state.password)
        }
    }

    private fun performSignUp(email: String, password: String) {
        execute(
            action = { signUpUseCase(SignUpUseCase.Params(email, password)) },
            onLoading = { newState { it.copy(isLoading = true) } },
            onSuccess = { _ ->
                newState { it.copy(isLoading = false) }
                navigate(SignUpNavEvent.ToHome)
            },
            onError = { error: BaseException ->
                // A taken email belongs on the email field; anything else is not about one field,
                // so it goes to the form-level error rather than being dropped.
                val emailTaken = error is EmailAlreadyExistsException
                newState {
                    it.copy(
                        isLoading = false,
                        emailError = if (emailTaken) SignUpEmailError.ALREADY_EXISTS else null,
                        error = if (emailTaken) null else error.toAppError(),
                    )
                }
            }
        )
    }

    fun toSignIn() {
        navigate(SignUpNavEvent.ToSignIn)
    }

    fun openPrivacy() {
        navigate(SignUpNavEvent.OpenPrivacy(PRIVACY_URL))
    }

    private fun validateEmail(email: String): SignUpEmailError? {
        return when {
            email.isBlank() -> SignUpEmailError.EMPTY
            !ValidationPatterns.isValidEmail(email) -> SignUpEmailError.INVALID_FORMAT
            else -> null
        }
    }

    private fun validatePassword(password: String): SignUpPasswordError? {
        return when {
            password.isBlank() -> SignUpPasswordError.EMPTY
            !ValidationPatterns.isPasswordLongEnough(password) -> SignUpPasswordError.TOO_SHORT
            !ValidationPatterns.isValidPassword(password) -> SignUpPasswordError.WEAK
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): SignUpConfirmPasswordError? {
        return when {
            confirmPassword.isBlank() -> SignUpConfirmPasswordError.EMPTY
            confirmPassword != password -> SignUpConfirmPasswordError.MISMATCH
            else -> null
        }
    }
}

enum class SignUpEmailError {
    EMPTY,
    INVALID_FORMAT,
    ALREADY_EXISTS
}

enum class SignUpPasswordError {
    EMPTY,
    TOO_SHORT,
    WEAK
}

enum class SignUpConfirmPasswordError {
    EMPTY,
    MISMATCH
}

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: SignUpEmailError? = null,
    val passwordError: SignUpPasswordError? = null,
    val confirmPasswordError: SignUpConfirmPasswordError? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null,
)

private const val PRIVACY_URL = "https://showcase.mkdigital.sk/privacy"

sealed interface SignUpNavEvent : NavEvent {
    data object ToHome : SignUpNavEvent
    data object ToSignIn : SignUpNavEvent
    data class OpenPrivacy(val url: String) : SignUpNavEvent
}

private class EmailAlreadyExistsException(
    override val errorCode: String = "5001",
    override val logMessage: String = "Sign-up rejected: email already registered",
    override val shouldReport: Boolean = false,
) : BaseException(message = "Email already exists")

