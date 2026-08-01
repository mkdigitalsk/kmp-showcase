package com.mk.kmpshowcase.presentation.base.router

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

actual class EmailRouterImpl(private val context: Context) : EmailRouter {
    actual override fun sendEmail(to: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
