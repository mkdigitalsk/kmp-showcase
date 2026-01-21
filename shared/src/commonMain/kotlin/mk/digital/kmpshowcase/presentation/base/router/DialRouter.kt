package mk.digital.kmpshowcase.presentation.base.router

interface DialRouter {

    fun dial(number: String)
}

expect class DialRouterImpl : DialRouter {
    override fun dial(number: String)
}
