package sk.mkdigital.kmpshowcase.presentation.screen.database

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.domain.model.NoteSortOption
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DatabaseScreenPreview(
    @PreviewParameter(DatabaseScreenPreviewParams::class) state: DatabaseUiState
) {
    AppTheme {
        DatabaseScreen(state = state)
    }
}

internal class DatabaseScreenPreviewParams : PreviewParameterProvider<DatabaseUiState> {
    override val values = sequenceOf(
        DatabaseUiState(isLoading = true),
        DatabaseUiState(error = true),
        DatabaseUiState(
            notes = listOf(
                Note(id = 1, title = "title", content = "content", createdAt = 0).toUiModel(),
                Note(id = 2, title = "title2", content = "content2", createdAt = 1769344378).toUiModel(),
            ),
            newNoteTitle = "New Note",
            newNoteContent = "Content",
            sortOption = NoteSortOption.DATE_ASC,
            showFilterMenu = true
        )
    )
}
