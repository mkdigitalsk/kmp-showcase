package sk.mkdigital.kmpshowcase.server.feature.user.service

/**
 * How the account's apps draw their colours.
 *
 * - [SYSTEM] — light or dark as the device is set.
 * - [LIGHT] — light whatever the device is set to.
 * - [DARK] — dark whatever the device is set to.
 */
internal enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}
