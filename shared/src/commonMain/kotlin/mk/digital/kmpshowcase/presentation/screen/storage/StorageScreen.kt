package mk.digital.kmpshowcase.presentation.screen.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mk.digital.kmpshowcase.presentation.component.buttons.OutlinedButton
import mk.digital.kmpshowcase.presentation.component.cards.AppElevatedCard
import mk.digital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import mk.digital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import mk.digital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import mk.digital.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import mk.digital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargeNeutral80
import mk.digital.kmpshowcase.presentation.foundation.floatingNavBarSpace
import mk.digital.kmpshowcase.presentation.foundation.space4
import mk.digital.kmpshowcase.shared.generated.resources.Res
import mk.digital.kmpshowcase.shared.generated.resources.storage_clear_session
import mk.digital.kmpshowcase.shared.generated.resources.storage_persistent_hint
import mk.digital.kmpshowcase.shared.generated.resources.storage_persistent_label
import mk.digital.kmpshowcase.shared.generated.resources.storage_session_hint
import mk.digital.kmpshowcase.shared.generated.resources.storage_session_label
import mk.digital.kmpshowcase.shared.generated.resources.storage_subtitle
import mk.digital.kmpshowcase.shared.generated.resources.storage_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun StorageScreen(viewModel: StorageViewModel) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space4,
            end = space4,
            top = space4,
            bottom = floatingNavBarSpace
        ),
        verticalArrangement = Arrangement.spacedBy(space4)
    ) {
        item {
            Column {
                TextHeadlineMediumPrimary(stringResource(Res.string.storage_title))
                TextBodyMediumNeutral80(stringResource(Res.string.storage_subtitle))
            }
        }

        item {
            CounterCard(
                label = stringResource(Res.string.storage_session_label),
                hint = stringResource(Res.string.storage_session_hint),
                counter = state.sessionCounter,
                onIncrement = viewModel::incrementSessionCounter,
                onDecrement = viewModel::decrementSessionCounter
            )
        }

        item {
            CounterCard(
                label = stringResource(Res.string.storage_persistent_label),
                hint = stringResource(Res.string.storage_persistent_hint),
                counter = state.persistentCounter,
                onIncrement = viewModel::incrementPersistentCounter,
                onDecrement = viewModel::decrementPersistentCounter
            )
        }

        item {
            OutlinedButton(
                text = stringResource(Res.string.storage_clear_session),
                onClick = viewModel::clearSession
            )
        }
    }
}

@Composable
private fun CounterCard(
    label: String,
    hint: String,
    counter: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
        TextBodyLargeNeutral80(label)
        Spacer2()
        TextBodyMediumNeutral80(hint)
        Spacer2()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space4)
        ) {
            IconButton(onClick = onDecrement) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrease",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            TextTitleLargeNeutral80(counter.toString())
            IconButton(onClick = onIncrement) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increase",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
