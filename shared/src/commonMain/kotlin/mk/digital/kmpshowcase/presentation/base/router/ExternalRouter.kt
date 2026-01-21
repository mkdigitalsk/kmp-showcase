package mk.digital.kmpshowcase.presentation.base.router

expect class ExternalRouter: DialRouter, LinkRouter, ShareRouter {
    override fun openLink(url: String)
    override fun dial(number: String)
    override fun share(text: String)
}
