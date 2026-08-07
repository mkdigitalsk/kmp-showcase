package sk.mkdigital.kmpshowcase.presentation.base.router

/**
 * The share sheet's link card is driven by LPLinkMetadata, which Kotlin/Native cannot override on
 * UIActivityItemSource. The iOS app installs a presenter at startup and this delegates to it.
 */
object IosShareSheet {
    var present: ((text: String, title: String, url: String) -> Unit)? = null
}

actual class ShareRouterImpl : ShareRouter {
    actual override fun share(
        text: String,
        title: String,
        url: String
    ) {
        IosShareSheet.present?.invoke(text, title, url)
    }
}
