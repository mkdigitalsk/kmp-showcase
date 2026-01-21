package mk.digital.kmpshowcase

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import mk.digital.kmpshowcase.presentation.base.NavRouter
import mk.digital.kmpshowcase.presentation.base.Navigation
import mk.digital.kmpshowcase.presentation.base.Navigation.HomeSection
import mk.digital.kmpshowcase.presentation.base.ScreenWrapper
import mk.digital.kmpshowcase.presentation.base.ToolbarConfig
import mk.digital.kmpshowcase.presentation.base.rememberNavEntryDecorators
import mk.digital.kmpshowcase.presentation.base.rememberNavRouter
import mk.digital.kmpshowcase.presentation.component.TopAppBar
import mk.digital.kmpshowcase.presentation.component.image.AppIcon
import mk.digital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLarge
import mk.digital.kmpshowcase.presentation.foundation.AppTheme
import mk.digital.kmpshowcase.presentation.foundation.appColors
import mk.digital.kmpshowcase.presentation.screen.detail.DetailScreen
import mk.digital.kmpshowcase.presentation.screen.detail.DetailViewModel
import mk.digital.kmpshowcase.presentation.screen.explore.ExploreScreen
import mk.digital.kmpshowcase.presentation.screen.explore.ExploreViewModel
import mk.digital.kmpshowcase.presentation.screen.home.HomeNavEvents
import mk.digital.kmpshowcase.presentation.screen.home.HomeScreen
import mk.digital.kmpshowcase.presentation.screen.home.HomeViewModel
import mk.digital.kmpshowcase.presentation.screen.profile.ProfileScreen
import mk.digital.kmpshowcase.presentation.screen.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


private val saveStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeSection.Home.serializer())
            subclass(HomeSection.Detail.serializer())
            subclass(Navigation.Explore.serializer())
            subclass(Navigation.Profile.serializer())
        }
    }
}

@Composable
fun MainView() {
    val router: NavRouter<Navigation> = rememberNavRouter(saveStateConfiguration, HomeSection.Home)
    val currentRoute: Navigation = router.backStack.last()
    val currentToolbar = remember { mutableStateOf<ToolbarConfig?>(null) }

    AppTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            topBar = {
                currentToolbar.value?.let { toolbar ->
                    TopAppBar(
                        title = toolbar.toolbarTitle(),
                        navIcon = toolbar.navIcon,
                        backClick = router::onBack,
                    )
                }
            },
            bottomBar = {
                BottomBarNavigation(
                    current = currentRoute,
                    onNavigate = { nav ->
                        val shouldNavigate = currentRoute != nav
                        if (shouldNavigate) {
                            val includeHome = nav is HomeSection.Home
                            router.navigateTo(
                                page = nav,
                                popUpTo = HomeSection.Home::class,
                                inclusive = includeHome
                            )
                        }
                    }
                )
            },
        ) { contentPadding ->
            // Callback for entries to set toolbar
            val onScreenChange: (ToolbarConfig?) -> Unit = { toolbar ->
                currentToolbar.value = toolbar
            }

            NavDisplay(
                modifier = Modifier.padding(contentPadding),
                backStack = router.backStack,
                onBack = router::onBack,
                entryDecorators = rememberNavEntryDecorators(),
                entryProvider = entryProvider {
                    entry<HomeSection.Home> {
                        val viewModel = koinViewModel<HomeViewModel>()
                        HomeNavEvents(viewModel, router)
                        ScreenWrapper(viewModel, onScreenChange) {
                            HomeScreen(viewModel)
                        }
                    }
                    entry<HomeSection.Detail> {
                        val viewModel = koinViewModel<DetailViewModel> {
                            parametersOf(it.id)
                        }
                        ScreenWrapper(viewModel, onScreenChange) {
                            DetailScreen(viewModel)
                        }
                    }
                    entry<Navigation.Explore> {
                        val viewModel = koinViewModel<ExploreViewModel>()
                        ScreenWrapper(viewModel, onScreenChange) {
                            ExploreScreen()
                        }
                    }
                    entry<Navigation.Profile> {
                        val viewModel = koinViewModel<ProfileViewModel>()
                        ScreenWrapper(viewModel, onScreenChange) {
                            ProfileScreen()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.AppBottomNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.appColors.neutral80
    NavigationBarItem(
        selected = selected,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
            indicatorColor = MaterialTheme.appColors.neutral20
        ),
        onClick = onClick,
        icon = {
            AppIcon(imageVector = icon)
        },
        label = {
            TextBodyLarge(text = label, color = Color.Unspecified)
        }
    )
}

@Composable
fun BottomBarNavigation(
    current: Navigation,
    onNavigate: (Navigation) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        AppBottomNavigationItem(
            selected = current is HomeSection.Home,
            onClick = { onNavigate(HomeSection.Home) },
            icon = Icons.Filled.Home,
            label = "Home"
        )
        AppBottomNavigationItem(
            selected = current is Navigation.Explore,
            onClick = { onNavigate(Navigation.Explore) },
            icon = Icons.Outlined.Search,
            label = "Explore"
        )
        AppBottomNavigationItem(
            selected = current is Navigation.Profile,
            onClick = { onNavigate(Navigation.Profile) },
            icon = Icons.Filled.Person,
            label = "Profile"
        )
    }
}
