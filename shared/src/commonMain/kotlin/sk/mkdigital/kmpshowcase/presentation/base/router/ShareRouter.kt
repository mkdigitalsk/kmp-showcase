package sk.mkdigital.kmpshowcase.presentation.base.router

interface ShareRouter {
    fun share(text: String, title: String, url: String)
}

expect class ShareRouterImpl : ShareRouter {
    override fun share(text: String, title: String, url: String)
}
