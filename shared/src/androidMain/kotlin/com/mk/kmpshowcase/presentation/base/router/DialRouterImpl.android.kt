package com.mk.kmpshowcase.presentation.base.router

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

actual class DialRouterImpl(private val context: Context) : DialRouter {
    actual override fun dial(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$number".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
