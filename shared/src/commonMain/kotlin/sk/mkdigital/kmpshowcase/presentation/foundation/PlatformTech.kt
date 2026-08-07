package sk.mkdigital.kmpshowcase.presentation.foundation

/**
 * What actually runs under the shared code on this platform. The showcase advertises its stack, so the
 * subtitles have to name the implementation the device is running, not both platforms' at once.
 */
expect object PlatformTech {
    val keyValueStore: String
    val httpEngine: String
}
