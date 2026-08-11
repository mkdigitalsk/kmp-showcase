package sk.mkdigital.kmpshowcase.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import sk.mkdigital.kmpshowcase.presentation.component.buttons.AppTextButtonPrimary
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import sk.mkdigital.kmpshowcase.presentation.foundation.appColorScheme
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.button_cancel
import sk.mkdigital.kmpshowcase.shared.generated.resources.button_ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppAlertDialog(
    text: String,
    onDismissRequest: () -> Unit,
    title: String? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.appColorScheme.neutral0,
        title = title?.let { { TextTitleLargePrimary(title) } },
        text = { TextBodyMediumNeutral80(text) },
        dismissButton = dismissButton,
        confirmButton = confirmButton
    )
}

@Composable
fun AppConfirmDialog(
    text: String,
    title: String? = null,
    onDismissRequest: () -> Unit,
) {
    AppAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = {
            AppTextButtonPrimary(text = stringResource(Res.string.button_ok), onClick = onDismissRequest)
        },
    )
}

@Composable
fun AppAlertDialog(
    text: String,
    onConfirm: () -> Unit,
    title: String? = null,
    confirmButtonText: String = stringResource(Res.string.button_ok),
    dismissButtonText: String = stringResource(Res.string.button_cancel),
    onDismissRequest: () -> Unit,
) {
    AppAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = {
            AppTextButtonPrimary(text = confirmButtonText, onClick = onConfirm)
        },
        dismissButton = {
            AppTextButtonPrimary(text = dismissButtonText, onClick = onDismissRequest)
        }
    )
}

@Composable
fun AppAlertDialog(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.appColorScheme.neutral0,
        title = { TextTitleLargePrimary(title) },
        text = content,
        confirmButton = {},
    )
}
