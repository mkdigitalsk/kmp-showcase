package com.mk.kmpshowcase.presentation.base.router

interface SettingsRouter {
    fun openSettings()
    fun openNotificationSettings()
}

expect class SettingsRouterImpl : SettingsRouter {
    override fun openSettings()
    override fun openNotificationSettings()
}
