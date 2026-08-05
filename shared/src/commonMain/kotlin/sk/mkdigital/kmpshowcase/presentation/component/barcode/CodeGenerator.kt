package sk.mkdigital.kmpshowcase.presentation.component.barcode

import androidx.compose.ui.graphics.ImageBitmap

expect class CodeGenerator() {
    fun generate(text: String, format: CodeFormat = CodeFormat.QR_CODE): ImageBitmap
}
