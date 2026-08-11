package sk.mkdigital.kmpshowcase.presentation.component.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import sk.mkdigital.kmpshowcase.presentation.component.AppAlertDialog
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.permission_allow
import sk.mkdigital.kmpshowcase.shared.generated.resources.permission_cancel
import sk.mkdigital.kmpshowcase.shared.generated.resources.permission_required
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun PermissionView(
    permission: PermissionType,
    onDeniedDialogDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val manifestPermission = permission.toManifestPermission()
    val state = if (manifestPermission == null) null else rememberPermissionState(manifestPermission)
    var rationaleDismissed by remember { mutableStateOf(false) }
    var hasRequested by remember { mutableStateOf(false) }

    if (state == null || state.status.isGranted) {
        content()
    } else if (state.status.shouldShowRationale && !rationaleDismissed) {
        PermissionRationaleUi(
            message = permission.rationaleMessage,
            onConfirm = { state.launchPermissionRequest() },
            onDismiss = {
                rationaleDismissed = true
                onDeniedDialogDismiss()
            }
        )
    } else {
        LaunchedEffect(manifestPermission, hasRequested) {
            if (!hasRequested && !rationaleDismissed) {
                hasRequested = true
                state.launchPermissionRequest()
            }
        }
        val context = LocalContext.current
        PermissionDenyUi(
            message = permission.deniedMessage,
            onConfirm = { launchSettings(context) },
        )
    }
}

private fun PermissionType.toManifestPermission() = when (this) {
    PermissionType.CAMERA -> Manifest.permission.CAMERA
    PermissionType.GALLERY -> null
    PermissionType.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
    PermissionType.NOTIFICATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else null
}

private fun launchSettings(context: Context) {
    Intent(
        /* action = */ Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        /* uri = */ Uri.fromParts("package", context.packageName, null)
    ).also {
        context.startActivity(it)
    }
}

@Composable
private fun PermissionRationaleUi(
    message: StringResource,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(Res.string.permission_required),
        text = stringResource(message),
        confirmButtonText = stringResource(Res.string.permission_allow),
        dismissButtonText = stringResource(Res.string.permission_cancel),
        onConfirm = onConfirm,
        onDismissRequest = onDismiss,
    )
}


