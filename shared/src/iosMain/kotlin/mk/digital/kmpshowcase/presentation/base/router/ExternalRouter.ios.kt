package mk.digital.kmpshowcase.presentation.base.router

actual class ExternalRouter :
    DialRouter by DialRouterImpl(),
    LinkRouter by LinkRouterImpl(),
    ShareRouter by ShareRouterImpl()
