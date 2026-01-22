package mk.digital.kmpshowcase.presentation.foundation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = LightColorPalette
    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colorScheme = colors.material,
            typography = Typography,
            shapes = MaterialTheme.shapes,
            content = {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize(),
                    content = content
                )
            }
        )
    }
}

private val LocalColors = staticCompositionLocalOf { LightColorPalette }

val MaterialTheme.appColorScheme: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

private val LightColorPalette = AppColors(
    material = lightColorScheme(
        primary = PrimaryLight,
        onPrimary = Neutral0Light,
        primaryContainer = PrimaryContainerLight,
        onPrimaryContainer = Neutral0Light,
        secondary = SecondaryLight,
        onSecondary = Neutral0Light,
        secondaryContainer = SecondaryContainerLight,
        onSecondaryContainer = Neutral0Light,
        background = BackgroundLight,
        onBackground = Neutral100Light,
        surface = SurfaceLight,
        onSurface = Neutral100Light,
        error = ErrorLight,
        onError = Neutral0Light,
        errorContainer = ErrorContainerLight,
        onErrorContainer = Neutral0Light,
        surfaceTint = Neutral0Light,
        surfaceVariant = Neutral80Light,
    ),
    neutral0 = Neutral0Light,
    neutral20 = Neutral20Light,
    neutral80 = Neutral80Light,
    neutral100 = Neutral100Light,
    success = SuccessLight,
    warning = WarningLight,
)
