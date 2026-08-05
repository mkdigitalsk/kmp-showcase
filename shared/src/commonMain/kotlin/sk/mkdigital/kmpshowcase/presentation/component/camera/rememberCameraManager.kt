package sk.mkdigital.kmpshowcase.presentation.component.camera

import sk.mkdigital.kmpshowcase.presentation.component.imagepicker.ImageResult
import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraManager(onResult: (ImageResult?) -> Unit): CameraManager

expect class CameraManager(
    onLaunch: () -> Unit
) {
    fun launch()
}
