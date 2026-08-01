package com.mk.kmpshowcase.presentation.base.router

import android.content.Context

actual class ExternalRouter(private val context: Context) :
    DialRouter by DialRouterImpl(context),
    LinkRouter by LinkRouterImpl(context),
    ShareRouter by ShareRouterImpl(context),
    CopyRouter by CopyRouterImpl(context),
    EmailRouter by EmailRouterImpl(context),
    SettingsRouter by SettingsRouterImpl(context)
