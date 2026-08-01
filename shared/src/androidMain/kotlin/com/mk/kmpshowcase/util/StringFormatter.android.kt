package com.mk.kmpshowcase.util

import java.util.Locale

actual object StringFormatter {
    actual fun formatDouble(value: Double, decimals: Int): String {
        return String.format(Locale.US, "%.${decimals}f", value)
    }
}
