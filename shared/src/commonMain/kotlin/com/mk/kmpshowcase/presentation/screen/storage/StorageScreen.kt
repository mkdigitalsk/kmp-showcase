package com.mk.kmpshowcase.presentation.screen.storage

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.buttons.OutlinedButton
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import com.mk.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargeNeutral80
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.storage_clear_session
import com.mk.kmpshowcase.shared.generated.resources.storage_persistent_hint
import com.mk.kmpshowcase.shared.generated.resources.storage_persistent_label
import com.mk.kmpshowcase.shared.generated.resources.storage_session_hint
import com.mk.kmpshowcase.shared.generated.resources.storage_session_label
import com.mk.kmpshowcase.shared.generated.resources.storage_subtitle
import com.mk.kmpshowcase.shared.generated.resources.storage_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun StorageScreen(viewModel: StorageViewModel = lifecycleAwareViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StorageScreen(
        state = state,
        onIncrementSession = viewModel::incrementSessionCounter,
        onDecrementSession = viewModel::decrementSessionCounter,
        onIncrementPersistent = viewModel::incrementPersistentCounter,
        onDecrementPersistent = viewModel::decrementPersistentCounter,
        onClearSession = viewModel::clearSession,
    )
}

@Composable
fun StorageScreen(
    state: StorageUiState,
    onIncrementSession: () -> Unit = {},
    onDecrementSession: () -> Unit = {},
    onIncrementPersistent: () -> Unit = {},
    onDecrementPersistent: () -> Unit = {},
    onClearSession: () -> Unit = {},
) {
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
                onIncrement = onIncrementSession,
                onDecrement = onDecrementSession
            )
        }

        item {
            CounterCard(
                label = stringResource(Res.string.storage_persistent_label),
                hint = stringResource(Res.string.storage_persistent_hint),
                counter = state.persistentCounter,
                onIncrement = onIncrementPersistent,
                onDecrement = onDecrementPersistent
            )
        }

        item {
            OutlinedButton(
                text = stringResource(Res.string.storage_clear_session),
                onClick = onClearSession
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
