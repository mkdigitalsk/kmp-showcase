package sk.mkdigital.kmpshowcase.presentation.screen.networking

import androidx.compose.runtime.Immutable
import sk.mkdigital.kmpshowcase.domain.useCase.GetUsersUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import sk.mkdigital.kmpshowcase.presentation.base.AppError
import sk.mkdigital.kmpshowcase.presentation.base.toAppError
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel

class NetworkingViewModel(
    private val getUsersUseCase: GetUsersUseCase
) : BaseViewModel<NetworkingUiState>(NetworkingUiState()) {

    override fun loadInitialData() {
        fetchUsers()
    }

    fun fetchUsers() {
        execute(
            action = { getUsersUseCase() },
            onLoading = { newState { it.copy(isLoading = true, error = null) } },
            onSuccess = { users ->
                newState { it.copy(isLoading = false, users = users.map { user -> user.toUiModel() }) }
            },
            onError = { error -> newState { it.copy(isLoading = false, error = error.toAppError()) } }
        )
    }

    fun refresh() {
        fetchUsers()
    }
}

@Immutable
data class NetworkingUiState(
    val isLoading: Boolean = false,
    val users: List<UserUiModel> = emptyList(),
    val error: AppError? = null
)
