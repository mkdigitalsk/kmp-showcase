package sk.mkdigital.kmpshowcase.presentation.base.router

interface EmailRouter {
    fun sendEmail(to: String, subject: String = "", body: String = "")
}

expect class EmailRouterImpl : EmailRouter {
    override fun sendEmail(to: String, subject: String, body: String)
}
