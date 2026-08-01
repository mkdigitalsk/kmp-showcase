package com.mk.kmpshowcase.presentation.base.router

actual class ExternalRouter :
    DialRouter by DialRouterImpl(),
    LinkRouter by LinkRouterImpl(),
    ShareRouter by ShareRouterImpl(),
    CopyRouter by CopyRouterImpl(),
    EmailRouter by EmailRouterImpl(),
    SettingsRouter by SettingsRouterImpl()
