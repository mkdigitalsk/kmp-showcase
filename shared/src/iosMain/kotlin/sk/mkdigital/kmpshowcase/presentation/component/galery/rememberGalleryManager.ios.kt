package sk.mkdigital.kmpshowcase.presentation.component.galery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import sk.mkdigital.kmpshowcase.presentation.component.imagepicker.ImageResult
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.getBytes
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberGalleryManager(onResult: (ImageResult?) -> Unit): GalleryManager {
    val galleryDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {

            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                val provider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
                picker.dismissViewControllerAnimated(true, null)

                if (provider == null) {
                    onResult(null)
                    return
                }

                // Ask for JPEG rather than the raw item, and for data rather than a UIImage: the
                // provider transcodes either way, but materialising the image costs the full-size
                // bitmap. Loading runs off the main thread, and Compose is touched back on it.
                provider.loadDataRepresentationForTypeIdentifier(JPEG_TYPE) { data, _ ->
                    val result = data?.toByteArray()
                        ?.let { bytes -> ImageResult(bytes, Image.makeFromEncoded(bytes).toComposeImageBitmap()) }

                    dispatch_async(dispatch_get_main_queue()) { onResult(result) }
                }
            }
        }
    }

    return remember {
        GalleryManager {
            // PHPicker runs out of process: it needs no photo permission and behaves the same under
            // Limited Photo Access, where UIImagePickerController returns nothing.
            val configuration = PHPickerConfiguration().apply {
                setFilter(PHPickerFilter.imagesFilter())
                setSelectionLimit(1)
            }
            val picker = PHPickerViewController(configuration).apply { setDelegate(galleryDelegate) }
            getRootViewController()?.presentViewController(picker, true, null)
        }
    }
}

actual class GalleryManager actual constructor(private val onLaunch: () -> Unit) {
    actual fun launch() {
        onLaunch()
    }
}

/**
 * The camera roll stores HEIC by default since the iPhone 11, and skiko ships no HEIC decoder on iOS —
 * `Image.makeFromEncoded` throws on it. See [skiko#942](https://github.com/JetBrains/skiko/issues/942).
 */
private const val JPEG_TYPE = "public.jpeg"

private fun NSData.toByteArray(): ByteArray {
    val byteArray = ByteArray(length.toInt())
    byteArray.usePinned { pinned ->
        getBytes(pinned.addressOf(0), length)
    }
    return byteArray
}

@Suppress("DEPRECATION")
private fun getRootViewController() = UIApplication.sharedApplication.keyWindow?.rootViewController
