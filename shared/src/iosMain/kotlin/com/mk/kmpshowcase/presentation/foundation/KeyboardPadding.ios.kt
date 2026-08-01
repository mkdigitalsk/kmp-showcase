package com.mk.kmpshowcase.presentation.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import kotlinx.cinterop.useContents
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSValue
import platform.UIKit.CGRectValue
import platform.UIKit.UIKeyboardFrameEndUserInfoKey
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIKeyboardWillShowNotification

actual fun Modifier.keyboardPadding(): Modifier = composed {
    var keyboardHeight by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    DisposableEffect(Unit) {
        val center = NSNotificationCenter.defaultCenter

        val showObserver = center.addObserverForName(
            name = UIKeyboardWillShowNotification,
            `object` = null,
            queue = null
        ) { notification: NSNotification? ->
            notification?.userInfo?.let { userInfo ->
                val keyboardFrame = userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue
                keyboardFrame?.let { nsValue ->
                    val rect = nsValue.CGRectValue()
                    val height = rect.useContents { size.height }
                    keyboardHeight = height.toFloat()
                }
            }
        }

        val hideObserver = center.addObserverForName(
            name = UIKeyboardWillHideNotification,
            `object` = null,
            queue = null
        ) { _: NSNotification? ->
            keyboardHeight = 0f
        }

        onDispose {
            center.removeObserver(showObserver)
            center.removeObserver(hideObserver)
        }
    }

    val bottomPadding = with(density) { keyboardHeight.toDp() }
    this.padding(bottom = bottomPadding)
}
