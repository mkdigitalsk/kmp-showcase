package mk.digital.kmpshowcase.presentation.screen.networking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.presentation.component.CircularProgress
import mk.digital.kmpshowcase.presentation.component.ErrorView
import mk.digital.kmpshowcase.presentation.component.LoadingView
import mk.digital.kmpshowcase.presentation.component.cards.AppElevatedCard
import mk.digital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import mk.digital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import mk.digital.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import mk.digital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargeNeutral80
import mk.digital.kmpshowcase.presentation.foundation.floatingNavBarSpace
import mk.digital.kmpshowcase.presentation.foundation.space4
import mk.digital.kmpshowcase.shared.generated.resources.Res
import mk.digital.kmpshowcase.shared.generated.resources.networking_empty
import mk.digital.kmpshowcase.shared.generated.resources.networking_subtitle
import mk.digital.kmpshowcase.shared.generated.resources.networking_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NetworkingScreen(viewModel: NetworkingViewModel) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading && state.users.isEmpty() -> LoadingView()
            state.error != null && state.users.isEmpty() -> ErrorView(
                message = state.error!!,
                onRetry = viewModel::refresh
            )
            state.users.isEmpty() -> EmptyContent()
            else -> UserListContent(
                users = state.users,
                isRefreshing = state.isLoading,
                onRefresh = viewModel::refresh
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextBodyMediumNeutral80(stringResource(Res.string.networking_empty))
    }
}

@Composable
private fun UserListContent(
    users: List<User>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header with refresh button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = space4, vertical = space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                TextHeadlineMediumPrimary(stringResource(Res.string.networking_title))
                TextBodyMediumNeutral80(stringResource(Res.string.networking_subtitle))
            }
            IconButton(onClick = onRefresh, enabled = !isRefreshing) {
                if (isRefreshing) {
                    CircularProgress()
                } else {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // User list
        LazyColumn(
            contentPadding = PaddingValues(
                start = space4,
                end = space4,
                bottom = floatingNavBarSpace
            ),
            verticalArrangement = Arrangement.spacedBy(space4)
        ) {
            items(users, key = { it.id }) { user ->
                UserCard(user = user)
            }
        }
    }
}

@Composable
private fun UserCard(user: User) {
    AppElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(space4)
    ) {
        TextTitleLargeNeutral80(user.name)
        Spacer2()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            TextBodyMediumNeutral80(
                text = user.email,
                modifier = Modifier.padding(start = space4)
            )
        }
        Spacer2()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            TextBodyMediumNeutral80(
                text = "${user.address.city}, ${user.address.street}",
                modifier = Modifier.padding(start = space4)
            )
        }
    }
}
