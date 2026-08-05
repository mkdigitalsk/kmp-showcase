package sk.mkdigital.kmpshowcase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import sk.mkdigital.kmpshowcase.data.push.DeepLinkHandler
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import sk.mkdigital.kmpshowcase.domain.useCase.settings.GetThemeModeUseCase
import sk.mkdigital.kmpshowcase.presentation.base.NavRouter
import sk.mkdigital.kmpshowcase.presentation.base.Route
import sk.mkdigital.kmpshowcase.presentation.base.Route.HomeSection
import sk.mkdigital.kmpshowcase.presentation.base.Route.Login
import sk.mkdigital.kmpshowcase.presentation.base.Route.Register
import sk.mkdigital.kmpshowcase.presentation.base.Route.Settings
import sk.mkdigital.kmpshowcase.presentation.base.rememberNavEntryDecorators
import sk.mkdigital.kmpshowcase.presentation.base.rememberNavRouter
import sk.mkdigital.kmpshowcase.presentation.component.AppFloatingNavBar
import sk.mkdigital.kmpshowcase.presentation.component.AppSnackbarHost
import sk.mkdigital.kmpshowcase.presentation.component.FloatingNavItem
import sk.mkdigital.kmpshowcase.presentation.component.TopAppBar
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode
import sk.mkdigital.kmpshowcase.presentation.foundation.floatingNavBarSpace
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.presentation.screen.calendar.CalendarScreen
import sk.mkdigital.kmpshowcase.presentation.screen.database.DatabaseScreen
import sk.mkdigital.kmpshowcase.presentation.screen.feature.UiComponentsScreen
import sk.mkdigital.kmpshowcase.presentation.screen.home.HomeScreen
import sk.mkdigital.kmpshowcase.presentation.screen.login.LoginScreen
import sk.mkdigital.kmpshowcase.presentation.screen.networking.NetworkingScreen
import sk.mkdigital.kmpshowcase.presentation.screen.notifications.NotificationsScreen
import sk.mkdigital.kmpshowcase.presentation.screen.platformapis.PlatformApisScreen
import sk.mkdigital.kmpshowcase.presentation.screen.register.RegisterScreen
import sk.mkdigital.kmpshowcase.presentation.screen.scanner.ScannerScreen
import sk.mkdigital.kmpshowcase.presentation.screen.settings.SettingsScreen
import sk.mkdigital.kmpshowcase.presentation.screen.storage.StorageScreen
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.nav_home
import sk.mkdigital.kmpshowcase.shared.generated.resources.nav_settings
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject


val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}


@Suppress("CognitiveComplexMethod")
@Composable
fun MainView(
    onSetLocale: ((String) -> Unit)? = null,
) {
    val router: NavRouter<Route> = rememberNavRouter()
    val currentRoute: Route = router.backStack.last()
    val snackbarHostState = remember { SnackbarHostState() }

    val getThemeModeUseCase = koinInject<GetThemeModeUseCase>()
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    LaunchedEffect(Unit) {
        themeMode = getThemeModeUseCase()
    }

    DeepLinkEffect(router)

    AppTheme(themeMode = themeMode) {
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    snackbarHost = {
                        AppSnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(bottom = floatingNavBarSpace)
                        )
                    },
                    contentWindowInsets = WindowInsets(0),
                    topBar = {
                        if (currentRoute.showTopBar) {
                            TopAppBar(
                                title = stringResource(currentRoute.titleRes),
                                navIcon = if (currentRoute.showBackArrow) Icons.AutoMirrored.Filled.ArrowBack else null,
                                backClick = router::onBack,
                            )
                        }
                    },
                ) { contentPadding ->
                    NavDisplay(
                        modifier = Modifier.padding(contentPadding),
                        backStack = router.backStack,
                        onBack = router::onBack,
                        entryDecorators = rememberNavEntryDecorators(),
                        entryProvider = entryProvider {
                            entry<Login> { LoginScreen(router) }
                            entry<Register> { RegisterScreen(router) }
                            entry<HomeSection.Home> { HomeScreen(router) }
                            entry<HomeSection.UiComponents> { UiComponentsScreen() }
                            entry<HomeSection.Networking> { NetworkingScreen() }
                            entry<HomeSection.Storage> { StorageScreen() }
                            entry<HomeSection.PlatformApis> { PlatformApisScreen(router) }
                            entry<HomeSection.Scanner> { ScannerScreen() }
                            entry<HomeSection.Database> { DatabaseScreen() }
                            entry<HomeSection.Calendar> { CalendarScreen() }
                            entry<HomeSection.Notifications> { NotificationsScreen(router) }
                            entry<Settings> {
                                SettingsScreen(
                                    router = router,
                                    onSetLocale = onSetLocale,
                                    onThemeChanged = { mode -> themeMode = mode },
                                )
                            }
                        }
                    )
                }

                AnimatedVisibility(
                    visible = currentRoute.showBottomNav,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .systemBarsPadding()
                        .padding(bottom = space4)
                ) {
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
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DeepLinkEffect(
    router: NavRouter<Route>,
    pushNotificationService: PushNotificationService = koinInject(),
) {
    LaunchedEffect(Unit) {
        pushNotificationService.deepLinks.collect { deepLink ->
            DeepLinkHandler.parseDeepLink(deepLink)?.let { route ->
                when (route) {
                    is HomeSection -> if (route != HomeSection.Home) {
                        router.navigateTo(route, popUpTo = HomeSection.Home::class)
                    }

                    is Settings -> router.navigateTo(route, popUpTo = HomeSection.Home::class)

                    Login,
                    Register -> router.navigateTo(route, popUpTo = HomeSection.Home::class, inclusive = true)
                }
            }
        }
    }
}
