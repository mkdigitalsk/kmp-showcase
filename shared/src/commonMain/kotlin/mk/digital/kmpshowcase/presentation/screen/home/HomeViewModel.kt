package mk.digital.kmpshowcase.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.domain.useCase.LoadHomeDataUseCase
import mk.digital.kmpshowcase.domain.useCase.TrackButtonClickUseCase
import mk.digital.kmpshowcase.domain.useCase.base.invoke
import mk.digital.kmpshowcase.presentation.base.BaseViewModel
import mk.digital.kmpshowcase.presentation.base.NavEvent

class HomeViewModel(
    private val loadHomeDataUseCase: LoadHomeDataUseCase,
    private val trackButtonClickUseCase: TrackButtonClickUseCase,
) : BaseViewModel<HomeUiState>(HomeUiState()) {

    @Composable
    override fun toolbarTitle(): String = "Home"
    override val navIcon: ImageVector? = null

    override fun loadInitialData() {
        execute(
            onLoading = { newState { it.copy(loading = true) } },
            action = { loadHomeDataUseCase() },
            onSuccess = { result -> newState { it.copy(loading = false, users = result) } },
            onError = { e -> newState { it.copy(loading = false, error = e.userMessage) } }
        )
    }

    fun onUserCard(id: Int) {
        execute(
            action = { trackButtonClickUseCase(id) },
            onSuccess = {
                newState { it.copy(buttonClicked = true) }
                navigate(HomeNavEvent.ToDetail(id))
            }
        )
    }
}

data class HomeUiState(
    val loading: Boolean = true,
    val users: List<User> = emptyList(),
    val buttonClicked: Boolean = false,
    val error: String? = null
)

sealed interface HomeNavEvent : NavEvent {
    data class ToDetail(val id: Int) : HomeNavEvent
}
