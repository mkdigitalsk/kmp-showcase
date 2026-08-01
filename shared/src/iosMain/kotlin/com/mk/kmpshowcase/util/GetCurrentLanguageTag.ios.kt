package com.mk.kmpshowcase.util

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier
import platform.Foundation.preferredLanguages

actual fun getCurrentLanguageTag(): String {
    NSLocale.preferredLanguages.firstOrNull()?.let { tag ->
        return (tag as String).replace("_", "-")
    }
    var tag = NSLocale.currentLocale.localeIdentifier
    val atIndex = tag.indexOf('@')
    if (atIndex >= 0) {
        tag = tag.take(atIndex)
    }
    return tag.replace("_", "-")
}
