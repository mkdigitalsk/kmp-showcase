package sk.mkdigital.kmpshowcase.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A language the account reads the apps in, by its key on the wire.
 *
 * - [SK] — Slovak.
 * - [EN] — English.
 */
@Serializable
enum class LanguageDTO {
    @SerialName("SK") SK,
    @SerialName("EN") EN,
    ;

    companion object {
        /** What a key this build does not know reads as. */
        val DEFAULT = EN
    }
}
