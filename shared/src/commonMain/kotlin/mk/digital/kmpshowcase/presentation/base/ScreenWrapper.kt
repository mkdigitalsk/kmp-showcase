package mk.digital.kmpshowcase.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

/**
 * Callback type for screen configuration changes (toolbar).
 */
typealias OnScreenChange = (ToolbarConfig?) -> Unit

/**
 * Wrapper composable that handles lifecycle and screen config callback.
 * Automatically extracts ToolbarConfig from viewModel.
 *
 * @param viewModel The ViewModel for the screen
 * @param onScreenChange Callback to notify parent about toolbar changes
 * @param content The screen content
 */
@Composable
fun ScreenWrapper(
    viewModel: BaseViewModel<*>,
    onScreenChange: OnScreenChange,
    content: @Composable () -> Unit
) {
    SideEffect {
        onScreenChange(viewModel as? ToolbarConfig)
    }

    (viewModel as? ScreenLifecycle)?.let { ScreenLifecycleEffect(it) }
    content()
}
