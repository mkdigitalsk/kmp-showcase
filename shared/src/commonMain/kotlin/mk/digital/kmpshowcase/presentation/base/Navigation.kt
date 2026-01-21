package mk.digital.kmpshowcase.presentation.base

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Navigation : NavKey {

    @Serializable
    sealed interface HomeSection : Navigation {
        @Serializable
        data object Home : HomeSection

        @Serializable
        data class Detail(val id: Int) : HomeSection
    }

    @Serializable
    data object Explore : Navigation

    @Serializable
    data object Profile : Navigation
}
