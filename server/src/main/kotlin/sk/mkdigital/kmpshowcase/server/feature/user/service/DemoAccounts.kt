package sk.mkdigital.kmpshowcase.server.feature.user.service

/**
 * The accounts the sign-in screen hands out, so anyone who opens the app can delete the one everybody
 * else is demoing with. Held here rather than as a column: whoever holds the token can call the
 * endpoint directly, so hiding the button in four clients is not the control.
 */
private val DEMO_EMAILS = setOf(
    "test01@mkdigital.sk",
    "test02@mkdigital.sk",
    "test03@mkdigital.sk",
)

internal fun isDemoAccount(email: String): Boolean = email.lowercase() in DEMO_EMAILS

internal fun demoAccountEmails(): Set<String> = DEMO_EMAILS
