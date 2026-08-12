package sk.mkdigital.kmpshowcase.presentation.component.imagepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import sk.mkdigital.kmpshowcase.presentation.component.camera.rememberCameraManager
import sk.mkdigital.kmpshowcase.presentation.component.galery.rememberGalleryManager
import sk.mkdigital.kmpshowcase.presentation.component.permission.PermissionType
import sk.mkdigital.kmpshowcase.presentation.component.permission.PermissionView
import sk.mkdigital.kmpshowcase.presentation.component.permission.galleryRequiresPermission

@Composable
@Suppress("LongParameterList")
fun ImagePickerView(
    state: ImagePickerState,
    onImageLoading: () -> Unit,
    onImageResult: (ImageResult?) -> Unit,
    onDialogDismiss: () -> Unit,
    onActionSelect: (PickerAction) -> Unit,
    onActionReset: () -> Unit,
) {
    val currentOnActionReset by rememberUpdatedState(onActionReset)

    val cameraManager = rememberCameraManager { result ->
        onImageLoading()
        onImageResult(result)
    }

    val galleryManager = rememberGalleryManager { result ->
        onImageLoading()
        onImageResult(result)
    }

    if (state.showOptionDialog) {
        ImageSourceOptionDialog(
            onDismissRequest = onDialogDismiss,
            onAction = onActionSelect,
        )
    }

    when (state.action) {
        PickerAction.Camera -> PermissionView(
            permission = PermissionType.CAMERA,
            onDeniedDialogDismiss = onActionReset,
        ) {
            LaunchedEffect(Unit) {
                cameraManager.launch()
                currentOnActionReset()
            }
        }

        PickerAction.Gallery -> {
            if (galleryRequiresPermission) {
                PermissionView(
                    permission = PermissionType.GALLERY,
                    onDeniedDialogDismiss = onActionReset,
                ) {
                    LaunchedEffect(Unit) {
                        galleryManager.launch()
                        currentOnActionReset()
                    }
                }
            } else {
                LaunchedEffect(state.action) {
                    galleryManager.launch()
                    currentOnActionReset()
                }
            }
        }

        PickerAction.None -> Unit
    }
}
