package mk.digital.kmpshowcase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import mk.digital.kmpshowcase.presentation.base.NavRouter
import mk.digital.kmpshowcase.presentation.base.Route
import mk.digital.kmpshowcase.presentation.base.Route.HomeSection
import mk.digital.kmpshowcase.presentation.base.Route.Settings
import mk.digital.kmpshowcase.presentation.base.WithViewModel
import mk.digital.kmpshowcase.presentation.base.rememberNavEntryDecorators
import mk.digital.kmpshowcase.presentation.base.rememberNavRouter
import mk.digital.kmpshowcase.presentation.component.AppFloatingNavBar
import mk.digital.kmpshowcase.presentation.component.AppSnackbarHost
import mk.digital.kmpshowcase.presentation.component.FloatingNavItem
import mk.digital.kmpshowcase.presentation.component.TopAppBar
import mk.digital.kmpshowcase.presentation.foundation.AppTheme
import mk.digital.kmpshowcase.presentation.screen.feature.NetworkingScreen
import mk.digital.kmpshowcase.presentation.screen.feature.PlatformApisScreen
import mk.digital.kmpshowcase.presentation.screen.feature.StorageScreen
import mk.digital.kmpshowcase.presentation.screen.feature.UiComponentsScreen
import mk.digital.kmpshowcase.presentation.screen.home.HomeNavEvents
import mk.digital.kmpshowcase.presentation.screen.home.HomeScreen
import mk.digital.kmpshowcase.presentation.screen.home.HomeViewModel
import mk.digital.kmpshowcase.presentation.screen.settings.SettingsScreen
import mk.digital.kmpshowcase.shared.generated.resources.Res
import mk.digital.kmpshowcase.shared.generated.resources.nav_home
import mk.digital.kmpshowcase.shared.generated.resources.nav_settings
import org.jetbrains.compose.resources.stringResource


val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

private val saveStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeSection.Home.serializer())
            subclass(HomeSection.UiComponents.serializer())
            subclass(HomeSection.Networking.serializer())
            subclass(HomeSection.Storage.serializer())
            subclass(HomeSection.PlatformApis.serializer())
            subclass(Settings.serializer())
        }
    }
}

@Composable
fun MainView() {
    val router: NavRouter<Route> = rememberNavRouter(saveStateConfiguration, HomeSection.Home)
    val currentRoute: Route = router.backStack.last()
    val snackbarHostState = remember { SnackbarHostState() }

    AppTheme {
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    snackbarHost = { AppSnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets(0),
                    topBar = {
                        TopAppBar(
                            title = stringResource(currentRoute.titleRes),
                            navIcon = if (currentRoute.showBackArrow) Icons.AutoMirrored.Filled.ArrowBack else null,
                            backClick = router::onBack,
                        )
                    },
                ) { contentPadding ->
                    NavDisplay(
                        modifier = Modifier.padding(contentPadding),
                        backStack = router.backStack,
                        onBack = router::onBack,
                        entryDecorators = rememberNavEntryDecorators(),
                        entryProvider = entryProvider {
                            entry<HomeSection.Home> {
                                WithViewModel<HomeViewModel> { viewModel ->
                                    HomeNavEvents(viewModel, router)
                                    HomeScreen(viewModel)
                                }
                            }
                            entry<HomeSection.UiComponents> { UiComponentsScreen() }
                            entry<HomeSection.Networking> { NetworkingScreen() }
                            entry<HomeSection.Storage> { StorageScreen() }
                            entry<HomeSection.PlatformApis> { PlatformApisScreen() }
                            entry<Settings> {
                                SettingsScreen()
                            }
                        }
                    )
                }

                AppFloatingNavBar(
                    items = listOf(
                        FloatingNavItem(
                            icon = Icons.Filled.Home,
                            label = stringResource(Res.string.nav_home),
                            selected = currentRoute is HomeSection,
                            onClick = {
                                if (currentRoute !is HomeSection.Home) {
                                    router.navigateTo(
                                        page = HomeSection.Home,
                                        popUpTo = HomeSection.Home::class,
                                        inclusive = true
                                    )
                                }
                            }
                        ),
                        FloatingNavItem(
                            icon = Icons.Filled.Settings,
                            label = stringResource(Res.string.nav_settings),
                            selected = currentRoute is Settings,
                            onClick = {
                                if (currentRoute !is Settings) {
                                    router.navigateTo(
                                        page = Settings,
                                        popUpTo = HomeSection.Home::class,
                                        inclusive = false
                                    )
                                }
                            }
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .systemBarsPadding()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

