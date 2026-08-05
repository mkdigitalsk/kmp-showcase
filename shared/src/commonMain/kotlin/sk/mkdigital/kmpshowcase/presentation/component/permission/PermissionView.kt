package sk.mkdigital.kmpshowcase.presentation.component.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import sk.mkdigital.kmpshowcase.presentation.component.buttons.ContainedButton
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.permission_allow
import sk.mkdigital.kmpshowcase.shared.generated.resources.permission_required
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun PermissionView(
    permission: PermissionType,
    onDeniedDialogDismiss: () -> Unit,
    content: @Composable () -> Unit,
)

@Composable
fun PermissionDenyUi(
    message: StringResource,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(space4),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextTitleLargePrimary(
            text = stringResource(Res.string.permission_required),
            textAlign = TextAlign.Center
        )
        Spacer4()
        TextBodyLargeNeutral80(
            text = stringResource(message),
            textAlign = TextAlign.Center
        )
        Spacer4()
        ContainedButton(
            id = Res.string.permission_allow,
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
