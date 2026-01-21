package mk.digital.kmpshowcase.presentation.base.router

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class LinkRouterImpl : LinkRouter {
    actual override fun openLink(url: String) {
        val nsUrl = NSURL(string = url)
        UIApplication.sharedApplication.openURL(
            nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = {
                if (!it) {
                    println("LinkRouter: Invalid URL: '$url'")
                }
            }
        )
    }
}
