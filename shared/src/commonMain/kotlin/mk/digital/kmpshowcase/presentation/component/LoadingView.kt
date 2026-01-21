package mk.digital.kmpshowcase.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kmp_showcase.shared.generated.resources.Res
import kmp_showcase.shared.generated.resources.loading
import mk.digital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import mk.digital.kmpshowcase.presentation.foundation.appColors
import mk.digital.kmpshowcase.presentation.foundation.space4
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadingView() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.appColors.neutral0)
            .padding(horizontal = space4),
        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgress()
        TextBodyLargeNeutral80(
            text = stringResource(Res.string.loading),
            modifier = Modifier.padding(start = space4)
        )
    }
}
