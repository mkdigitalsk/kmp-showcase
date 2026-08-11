package sk.mkdigital.kmpshowcase.presentation.base.router

import sk.mkdigital.kmpshowcase.util.Logger

actual class ExternalRouter(logger: Logger) :
    DialRouter by DialRouterImpl(),
    LinkRouter by LinkRouterImpl(logger),
    ShareRouter by ShareRouterImpl(),
    CopyRouter by CopyRouterImpl(),
    EmailRouter by EmailRouterImpl(),
    SettingsRouter by SettingsRouterImpl()
