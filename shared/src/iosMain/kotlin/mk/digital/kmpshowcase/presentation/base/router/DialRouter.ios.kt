package mk.digital.kmpshowcase.presentation.base.router

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class DialRouterImpl : DialRouter {
    actual override fun dial(number: String) {
        val url = NSURL.URLWithString("tel:$number") ?: return
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(
                url = url,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }
}
