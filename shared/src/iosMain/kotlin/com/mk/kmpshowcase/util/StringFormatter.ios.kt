package com.mk.kmpshowcase.util

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual object StringFormatter {
    actual fun formatDouble(value: Double, decimals: Int): String {
        return NSString.stringWithFormat("%.${decimals}f", value)
    }
}
