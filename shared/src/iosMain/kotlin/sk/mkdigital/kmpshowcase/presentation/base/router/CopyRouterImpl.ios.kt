package sk.mkdigital.kmpshowcase.presentation.base.router

import platform.UIKit.UIPasteboard

actual class CopyRouterImpl : CopyRouter {
    actual override fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
