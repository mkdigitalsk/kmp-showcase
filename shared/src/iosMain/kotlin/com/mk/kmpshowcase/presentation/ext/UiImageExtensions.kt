package com.mk.kmpshowcase.presentation.ext

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.get
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetAlphaInfo
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageRelease
import platform.UIKit.UIImage

private const val BYTES_PER_PIXEL = 4
private const val BLUE_CHANNEL_OFFSET = 2


fun UIImage.toSkiaImage(): Image? {
    val imageRef = this.CGImage ?: return null

    val width = CGImageGetWidth(imageRef).toInt()
    val height = CGImageGetHeight(imageRef).toInt()

    val bytesPerRow = CGImageGetBytesPerRow(imageRef)
    val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
    val bytePointer = CFDataGetBytePtr(data)
    val length = CFDataGetLength(data)

    val alphaType = when (CGImageGetAlphaInfo(imageRef)) {
        CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst,
        CGImageAlphaInfo.kCGImageAlphaPremultipliedLast -> ColorAlphaType.PREMUL

        CGImageAlphaInfo.kCGImageAlphaFirst,
        CGImageAlphaInfo.kCGImageAlphaLast -> ColorAlphaType.UNPREMUL

        CGImageAlphaInfo.kCGImageAlphaNone,
        CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst,
        CGImageAlphaInfo.kCGImageAlphaNoneSkipLast -> ColorAlphaType.OPAQUE

        else -> ColorAlphaType.UNKNOWN
    }

    val byteArray = ByteArray(length.toInt()) { index ->
        bytePointer!![index].toByte()
    }

    CFRelease(data)
    CGImageRelease(imageRef)

    val skiaColorSpace = ColorSpace.sRGB
    val colorType = ColorType.RGBA_8888

    // Convert RGBA to BGRA (swap R and B channels)
    for (i in byteArray.indices step BYTES_PER_PIXEL) {
        val r = byteArray[i]
        val b = byteArray[i + BLUE_CHANNEL_OFFSET]
        byteArray[i] = b
        byteArray[i + BLUE_CHANNEL_OFFSET] = r
    }

    return Image.makeRaster(
        imageInfo = ImageInfo(
            width = width,
            height = height,
            colorType = colorType,
            alphaType = alphaType,
            colorSpace = skiaColorSpace
        ),
        bytes = byteArray,
        rowBytes = bytesPerRow.toInt(),
    )
}

fun UIImage.toImageBitmap(): ImageBitmap {
    val skiaImage = this.toSkiaImage() ?: return ImageBitmap(1, 1)
    return skiaImage.toComposeImageBitmap()
}
