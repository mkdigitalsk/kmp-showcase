package com.mk.kmpshowcase.presentation.base.router

expect class ExternalRouter :
    DialRouter,
    LinkRouter,
    ShareRouter,
    CopyRouter,
    EmailRouter,
    SettingsRouter {
    override fun openLink(url: String)
    override fun dial(number: String)
    override fun share(text: String)
    override fun copyToClipboard(text: String)
    override fun sendEmail(to: String, subject: String, body: String)
    override fun openSettings()
    override fun openNotificationSettings()
}
