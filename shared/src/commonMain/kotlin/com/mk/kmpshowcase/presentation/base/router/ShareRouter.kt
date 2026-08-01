package com.mk.kmpshowcase.presentation.base.router

interface ShareRouter {
    fun share(text: String)
}

expect class ShareRouterImpl : ShareRouter {
    override fun share(text: String)
}
