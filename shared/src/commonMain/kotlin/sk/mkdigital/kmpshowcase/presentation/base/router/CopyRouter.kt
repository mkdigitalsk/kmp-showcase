package sk.mkdigital.kmpshowcase.presentation.base.router

interface CopyRouter {
    fun copyToClipboard(text: String)
}

expect class CopyRouterImpl : CopyRouter {
    override fun copyToClipboard(text: String)
}
