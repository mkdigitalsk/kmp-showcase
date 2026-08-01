package com.mk.kmpshowcase.presentation.component.barcode

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import com.mk.kmpshowcase.presentation.ext.toImageBitmap
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.createCGImage
import platform.CoreImage.filterWithName
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.Foundation.setValue
import platform.UIKit.UIImage

private const val IMAGE_SCALE_FACTOR = 10.0

actual class CodeGenerator {

    actual fun generate(text: String, format: CodeFormat): ImageBitmap {
        val data = text.encodeToByteArray().usePinned {
            NSData.dataWithBytes(
                bytes = it.addressOf(0),
                length = it.get().size.toULong()
            )
        }

        val filterName = when (format) {
            CodeFormat.BARCODE -> "CICode128BarcodeGenerator"
            CodeFormat.QR_CODE -> "CIQRCodeGenerator"
        }

        val filter = CIFilter.filterWithName(filterName)!!
        filter.setValue(data, forKey = "inputMessage")
        val outputImage = filter.outputImage
            ?.imageByApplyingTransform(CGAffineTransformMakeScale(IMAGE_SCALE_FACTOR, IMAGE_SCALE_FACTOR))
            ?: error("No image generated")

        val context = CIContext.context()
        val cgImage = context.createCGImage(outputImage, outputImage.extent) ?: error("No CGImage")

        val uiImage: UIImage = UIImage.imageWithCGImage(cgImage)
        return uiImage.toImageBitmap()
    }
}
