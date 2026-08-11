package sk.mkdigital.kmpshowcase.presentation.base.router

import sk.mkdigital.kmpshowcase.util.Logger
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class LinkRouterImpl(private val logger: Logger) : LinkRouter {
    actual override fun openLink(url: String) {
        val nsUrl = NSURL(string = url)
        UIApplication.sharedApplication.openURL(
            nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = {
                if (!it) {
                    logger.e("LinkRouter: Invalid URL: '$url'")
                }
            }
        )
    }
}
