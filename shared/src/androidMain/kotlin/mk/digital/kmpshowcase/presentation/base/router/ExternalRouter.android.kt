package mk.digital.kmpshowcase.presentation.base.router

import android.content.Context

actual class ExternalRouter(private val context: Context) :
    DialRouter by DialRouterImpl(context),
    LinkRouter by LinkRouterImpl(context),
    ShareRouter by ShareRouterImpl(context)
