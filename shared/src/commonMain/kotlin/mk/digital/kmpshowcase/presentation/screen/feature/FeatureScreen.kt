package mk.digital.kmpshowcase.presentation.screen.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mk.digital.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import mk.digital.kmpshowcase.shared.generated.resources.Res
import mk.digital.kmpshowcase.shared.generated.resources.screen_networking
import mk.digital.kmpshowcase.shared.generated.resources.screen_platform_apis
import mk.digital.kmpshowcase.shared.generated.resources.screen_storage
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NetworkingScreen() {
    PlaceholderScreen(Res.string.screen_networking)
}

@Composable
fun StorageScreen() {
    PlaceholderScreen(Res.string.screen_storage)
}

@Composable
fun PlatformApisScreen() {
    PlaceholderScreen(Res.string.screen_platform_apis)
}

@Composable
internal fun PlaceholderScreen(titleRes: StringResource) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextHeadlineMediumPrimary(stringResource(titleRes))
    }
}
