package com.mk.kmpshowcase.presentation.component.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import com.mk.kmpshowcase.presentation.component.imagepicker.ImageResult
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.getBytes
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@Composable
actual fun rememberCameraManager(onResult: (ImageResult?) -> Unit): CameraManager {
    val imagePicker = UIImagePickerController()

    val cameraDelegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {

            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage
                    ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

                val result = image?.let { uiImage ->
                    UIImageJPEGRepresentation(uiImage, 0.8)?.let { nsData ->
                        val byteArray = nsData.toByteArray()
                        val imageBitmap = Image.makeFromEncoded(byteArray).toComposeImageBitmap()
                        ImageResult(byteArray, imageBitmap)
                    }
                }

                onResult(result)
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                onResult(null)
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }

    return remember {
        CameraManager {
            imagePicker.setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
            imagePicker.setAllowsEditing(true)
            imagePicker.setDelegate(cameraDelegate)
            getRootViewController()?.presentViewController(imagePicker, true, null)
        }
    }
}

actual class CameraManager actual constructor(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

private fun NSData.toByteArray(): ByteArray {
    val byteArray = ByteArray(length.toInt())
    byteArray.usePinned { pinned ->
        getBytes(pinned.addressOf(0), length)
    }
    return byteArray
}

@Suppress("DEPRECATION")
private fun getRootViewController() = UIApplication.sharedApplication.keyWindow?.rootViewController
