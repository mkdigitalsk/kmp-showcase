package sk.mkdigital.kmpshowcase.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import sk.mkdigital.kmpshowcase.presentation.component.buttons.ContainedButton
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.button_retry
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier.padding(space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextHeadlineMediumPrimary(stringResource(Res.string.error_title))
        Spacer2()
        TextBodyMediumNeutral80(message)
        if (onRetry != null) {
            Spacer2()
            ContainedButton(
                text = stringResource(Res.string.button_retry),
                onClick = onRetry
            )
        }
    }
}
