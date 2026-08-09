package sk.mkdigital.kmpshowcase.presentation.base

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_home
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_networking
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_scanner
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_platform_apis
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_settings
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_storage
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_calendar
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_database
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_notifications
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_ui_components
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_sign_in
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_sign_up
import sk.mkdigital.kmpshowcase.shared.generated.resources.screen_example
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface Route : NavKey {
    val titleRes: StringResource
    val showBackArrow: Boolean get() = true
    val showTopBar: Boolean get() = true
    val showBottomNav: Boolean get() = true

    @Serializable
    data object SignIn : Route {
        override val titleRes = Res.string.screen_sign_in
        override val showBackArrow = false
        override val showTopBar = false
        override val showBottomNav = false
    }

    @Serializable
    data object SignUp : Route {
        override val titleRes = Res.string.screen_sign_up
        override val showBackArrow = true
        override val showTopBar = false
        override val showBottomNav = false
    }

    @Serializable
    sealed interface HomeSection : Route {
        @Serializable
        data object Home : HomeSection {
            override val titleRes = Res.string.screen_home
            override val showBackArrow = false
        }

        @Serializable
        data object UiComponents : HomeSection {
            override val titleRes = Res.string.screen_ui_components
        }

        @Serializable
        data object Networking : HomeSection {
            override val titleRes = Res.string.screen_networking
        }

        @Serializable
        data object Storage : HomeSection {
            override val titleRes = Res.string.screen_storage
        }

        @Serializable
        data object PlatformApis : HomeSection {
            override val titleRes = Res.string.screen_platform_apis
        }

        @Serializable
        data object Scanner : HomeSection {
            override val titleRes = Res.string.screen_scanner
        }

        @Serializable
        data object Database : HomeSection {
            override val titleRes = Res.string.screen_database
        }

        @Serializable
        data object Calendar : HomeSection {
            override val titleRes = Res.string.screen_calendar
        }

        @Serializable
        data object Notifications : HomeSection {
            override val titleRes = Res.string.screen_notifications
        }

        @Serializable
        data object Example : HomeSection {
            override val titleRes = Res.string.screen_example
        }
    }

    @Serializable
    data object Settings : Route {
        override val titleRes = Res.string.screen_settings
        override val showBackArrow = false
    }
}
