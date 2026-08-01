package com.mk.kmpshowcase.presentation.base.router

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

actual class CopyRouterImpl(private val context: Context) : CopyRouter {
    actual override fun copyToClipboard(text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clip)
    }
}
