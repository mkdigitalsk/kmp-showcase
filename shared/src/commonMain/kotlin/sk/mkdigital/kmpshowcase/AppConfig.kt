package sk.mkdigital.kmpshowcase

data class AppConfig(
    val buildType: BuildType,
    val versionName: String,
    val versionCode: String,
    val baseUrl: String,
)
