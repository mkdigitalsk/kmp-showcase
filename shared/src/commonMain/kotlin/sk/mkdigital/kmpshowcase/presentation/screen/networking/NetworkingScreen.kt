package sk.mkdigital.kmpshowcase.presentation.screen.networking

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import sk.mkdigital.kmpshowcase.presentation.base.lifecycleAwareViewModel
import sk.mkdigital.kmpshowcase.presentation.component.AppAlertDialog
import sk.mkdigital.kmpshowcase.presentation.component.AppTextField
import sk.mkdigital.kmpshowcase.presentation.component.ErrorView
import sk.mkdigital.kmpshowcase.presentation.component.LoadingView
import sk.mkdigital.kmpshowcase.presentation.component.buttons.ContainedButton
import sk.mkdigital.kmpshowcase.presentation.component.cards.AppElevatedCard
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import sk.mkdigital.kmpshowcase.presentation.component.text
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargeNeutral80
import sk.mkdigital.kmpshowcase.presentation.foundation.floatingNavBarSpace
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_add
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_cancel
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_conflict_discard
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_conflict_keep
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_conflict_text
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_conflict_title
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_content
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_delete
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_edit
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_empty
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_refresh
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_save
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_subtitle
import sk.mkdigital.kmpshowcase.shared.generated.resources.networking_note_title

@Composable
fun NetworkingScreen(viewModel: NetworkingViewModel = lifecycleAwareViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NetworkingScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onCreate = viewModel::createNote,
        onStartEditing = viewModel::startEditing,
        onCancelEditing = viewModel::cancelEditing,
        onSave = viewModel::updateNote,
        onDelete = viewModel::deleteNote,
        onKeepMine = viewModel::overwriteConflict,
        onDiscardMine = viewModel::discardConflict,
    )
}

@Composable
fun NetworkingScreen(
    state: NetworkingUiState,
    onRefresh: () -> Unit = {},
    onCreate: (String, String) -> Unit = { _, _ -> },
    onStartEditing: (RemoteNoteUiModel) -> Unit = {},
    onCancelEditing: () -> Unit = {},
    onSave: (Long, String, String, String) -> Unit = { _, _, _, _ -> },
    onDelete: (Long) -> Unit = {},
    onKeepMine: (String, String) -> Unit = { _, _ -> },
    onDiscardMine: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NotesList(
            state = state,
            onRefresh = onRefresh,
            onCreate = onCreate,
            onStartEditing = onStartEditing,
            onCancelEditing = onCancelEditing,
            onSave = onSave,
            onDelete = onDelete,
        )

        state.conflict?.let { conflict ->
            ConflictDialog(
                conflict = conflict,
                draft = state.editing,
                onKeepMine = onKeepMine,
                onDiscardMine = onDiscardMine,
            )
        }
    }
}

@Composable
private fun NotesList(
    state: NetworkingUiState,
    onRefresh: () -> Unit,
    onCreate: (String, String) -> Unit,
    onStartEditing: (RemoteNoteUiModel) -> Unit,
    onCancelEditing: () -> Unit,
    onSave: (Long, String, String, String) -> Unit,
    onDelete: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(space4, space4, space4, floatingNavBarSpace),
        verticalArrangement = Arrangement.spacedBy(space4),
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextBodyLargeNeutral80(text = stringResource(Res.string.networking_subtitle))
                IconButton(
                    onClick = onRefresh,
                    enabled = !state.isLoading,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(Res.string.networking_refresh))
                }
            }
        }

        item { CreateNoteCard(isSaving = state.isSaving, isLoading = state.isLoading, onCreate = onCreate) }

        if (state.notes.isEmpty()) {
            item {
                when {
                    state.isLoading -> LoadingView()
                    state.error != null -> ErrorView(message = state.error.text(), onRetry = onRefresh)
                    else -> TextBodyMediumNeutral80(text = stringResource(Res.string.networking_empty))
                }
            }
        }

        items(state.notes, key = { it.id }) { note ->
            if (state.editing?.id == note.id) {
                EditNoteCard(
                    note = state.editing,
                    isSaving = state.isSaving,
                    onCancel = onCancelEditing,
                    onSave = onSave,
                )
            } else {
                NoteCard(
                    note = note,
                    onEdit = { onStartEditing(note) },
                    onDelete = { onDelete(note.id) },
                )
            }
        }
    }
}

@Composable
private fun CreateNoteCard(isSaving: Boolean, isLoading: Boolean, onCreate: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AppElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(space4)) {
            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = stringResource(Res.string.networking_note_title),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer2()
            AppTextField(
                value = content,
                onValueChange = { content = it },
                label = stringResource(Res.string.networking_content),
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer2()
            ContainedButton(
                text = stringResource(Res.string.networking_add),
                onClick = {
                    onCreate(title, content)
                    title = ""
                    content = ""
                },
                enabled = title.isNotBlank() && !isSaving && !isLoading,
                loading = isSaving,
            )
        }
    }
}

@Composable
private fun NoteCard(note: RemoteNoteUiModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    AppElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(space4), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                TextTitleLargeNeutral80(text = note.title)
                TextBodyMediumNeutral80(text = note.content)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.networking_edit))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.networking_delete))
            }
        }
    }
}

@Composable
private fun EditNoteCard(
    note: RemoteNoteUiModel,
    isSaving: Boolean,
    onCancel: () -> Unit,
    onSave: (Long, String, String, String) -> Unit,
) {
    var title by remember(note.id) { mutableStateOf(note.title) }
    var content by remember(note.id) { mutableStateOf(note.content) }

    AppElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(space4)) {
            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = stringResource(Res.string.networking_note_title),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer2()
            AppTextField(
                value = content,
                onValueChange = { content = it },
                label = stringResource(Res.string.networking_content),
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer2()
            Row(horizontalArrangement = Arrangement.spacedBy(space4)) {
                ContainedButton(text = stringResource(Res.string.networking_cancel), onClick = onCancel)
                ContainedButton(
                    text = stringResource(Res.string.networking_save),
                    onClick = { onSave(note.id, title, content, note.etag) },
                    enabled = !isSaving,
                    loading = isSaving,
                )
            }
        }
    }
}

@Composable
private fun ConflictDialog(
    conflict: RemoteNoteUiModel,
    draft: RemoteNoteUiModel?,
    onKeepMine: (String, String) -> Unit,
    onDiscardMine: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(Res.string.networking_conflict_title),
        text = stringResource(Res.string.networking_conflict_text, conflict.title),
        onDismissRequest = onDiscardMine,
        dismissButton = {
            ContainedButton(
                text = stringResource(Res.string.networking_conflict_discard),
                onClick = onDiscardMine,
            )
        },
        confirmButton = {
            ContainedButton(
                text = stringResource(Res.string.networking_conflict_keep),
                onClick = { draft?.let { onKeepMine(it.title, it.content) } },
            )
        },
    )
}
