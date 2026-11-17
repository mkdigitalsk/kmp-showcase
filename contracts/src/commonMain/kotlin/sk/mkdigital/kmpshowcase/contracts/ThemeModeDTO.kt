package sk.mkdigital.kmpshowcase.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * How the account's apps draw their colours.
 *
 * - [SYSTEM] — light or dark as the device is set.
 * - [LIGHT] — light whatever the device is set to.
 * - [DARK] — dark whatever the device is set to.
 */
@Serializable
enum class ThemeModeDTO {
    @SerialName("system") SYSTEM,
    @SerialName("light") LIGHT,
    @SerialName("dark") DARK,
}
