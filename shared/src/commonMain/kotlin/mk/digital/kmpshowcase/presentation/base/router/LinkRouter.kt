package mk.digital.kmpshowcase.presentation.base.router

interface LinkRouter {
    fun openLink(url: String)
}

expect class LinkRouterImpl : LinkRouter {
    override fun openLink(url: String)
}

