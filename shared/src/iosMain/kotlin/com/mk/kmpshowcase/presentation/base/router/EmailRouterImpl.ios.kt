package com.mk.kmpshowcase.presentation.base.router

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class EmailRouterImpl : EmailRouter {
    actual override fun sendEmail(to: String, subject: String, body: String) {
        val encodedSubject = subject.encodeURLComponent()
        val encodedBody = body.encodeURLComponent()
        val urlString = "mailto:$to?subject=$encodedSubject&body=$encodedBody"

        NSURL.URLWithString(urlString)?.let { url ->
            UIApplication.sharedApplication.openURL(url, emptyMap<Any?, Any?>()) { _ -> }
        }
    }

    private fun String.encodeURLComponent(): String {
        return this.replace(" ", "%20")
            .replace("&", "%26")
            .replace("=", "%3D")
            .replace("?", "%3F")
            .replace("\n", "%0A")
    }
}
