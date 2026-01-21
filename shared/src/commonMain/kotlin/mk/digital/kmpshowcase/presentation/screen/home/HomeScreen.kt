package mk.digital.kmpshowcase.presentation.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.presentation.base.CollectNavEvents
import mk.digital.kmpshowcase.presentation.base.NavRouter
import mk.digital.kmpshowcase.presentation.base.Navigation
import mk.digital.kmpshowcase.presentation.component.LoadingView
import mk.digital.kmpshowcase.presentation.component.cards.AppElevatedCard
import mk.digital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import mk.digital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer8
import mk.digital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import mk.digital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import mk.digital.kmpshowcase.presentation.foundation.space4

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column {
        if (state.loading) {
            LoadingView()
        } else {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(space4)) {
                Spacer4()
                TextBodyLargeNeutral80("users")
                Spacer4()
                state.users.forEach {
                    Spacer4()
                    UserCard(it) {
                        viewModel.onUserCard(it.id)
                    }
                }
                Spacer8()
            }
        }
    }
}

@Composable
private fun UserCard(user: User, onClick: () -> Unit) {
    AppElevatedCard(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(space4)) {
        TextBodyLargeNeutral80(user.name)
        Spacer4()
        TextBodyMediumNeutral80(user.email)
    }
}

@Composable
fun HomeNavEvents(
    viewModel: HomeViewModel,
    router: NavRouter<Navigation>
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) {
        if (it !is HomeNavEvent) return@CollectNavEvents
        when (it) {
            is HomeNavEvent.ToDetail -> router.navigateTo(Navigation.HomeSection.Detail(it.id))
        }
    }
}
