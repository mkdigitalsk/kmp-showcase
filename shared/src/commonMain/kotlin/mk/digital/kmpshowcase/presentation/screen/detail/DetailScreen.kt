package mk.digital.kmpshowcase.presentation.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mk.digital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import mk.digital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import mk.digital.kmpshowcase.presentation.foundation.space4

@Composable
fun DetailScreen(viewModel: DetailViewModel) {
    Column {
        Column(Modifier.padding(space4)) {
            Spacer4()
            TextBodyLargeNeutral80("Detail: ${viewModel.id}")
        }
    }
}
