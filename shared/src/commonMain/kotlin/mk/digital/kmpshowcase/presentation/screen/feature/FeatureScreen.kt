package mk.digital.kmpshowcase.presentation.screen.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mk.digital.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import mk.digital.kmpshowcase.shared.generated.resources.Res
import mk.digital.kmpshowcase.shared.generated.resources.screen_platform_apis
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlatformApisScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextHeadlineMediumPrimary(stringResource(Res.string.screen_platform_apis))
    }
}
