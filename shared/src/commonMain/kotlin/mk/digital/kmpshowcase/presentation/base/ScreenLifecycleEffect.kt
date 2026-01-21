package mk.digital.kmpshowcase.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.Flow

/**
 * A composable that provides lifecycle hooks for screens in Navigation 3 pattern.
 *
 * This tracks BOTH:
 * 1. Composition lifecycle - when screen enters/exits composition (navigation between screens)
 * 2. Activity lifecycle - when app goes to background/foreground
 *
 * @param key Unique key for this screen instance (e.g., route or component hashCode)
 * @param onCreate Called when the screen first enters the composition (only once per screen instance)
 * @param onResume Called when the screen becomes visible (composition enter OR app resumes from background)
 * @param onPause Called when the screen becomes invisible (composition exit OR app goes to background)
 * @param onDispose Called when the composable leaves the composition
 */
@Composable
private fun ScreenLifecycleEffect(
    key: Any? = Unit,
    onCreate: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onDispose: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnCreate by rememberUpdatedState(onCreate)
    val currentOnResume by rememberUpdatedState(onResume)
    val currentOnPause by rememberUpdatedState(onPause)
    val currentOnDispose by rememberUpdatedState(onDispose)

    // Track if we've already called onResume (to avoid double calls)
    var isResumed by remember(key) { mutableStateOf(false) }

    // Track if onCreate was called - resets each time screen enters composition
    // This ensures loadInitialData() is called every time user navigates to the screen
    var isFirstComposition by remember(key) { mutableStateOf(true) }

    // Handle screen entering composition (navigation)
    LaunchedEffect(key) {
        // onCreate only on first composition
        if (isFirstComposition) {
            isFirstComposition = false
            currentOnCreate()
        }

        // onResume when entering composition (if not already resumed)
        if (!isResumed && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            isResumed = true
            currentOnResume()
        }
    }

    // Handle screen leaving composition (navigation away)
    DisposableEffect(key) {
        onDispose {
            if (isResumed) {
                isResumed = false
                currentOnPause()
            }
            currentOnDispose()
        }
    }

    // Handle app background/foreground lifecycle
    DisposableEffect(lifecycleOwner, key) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (!isResumed) {
                        isResumed = true
                        currentOnResume()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (isResumed) {
                        isResumed = false
                        currentOnPause()
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * A composable that connects a ScreenLifecycle to lifecycle events.
 * Uses the viewModel's hashCode as key to properly track each screen instance separately.
 * @param viewModel The ScreenLifecycle instance (typically a ViewModel) to connect to lifecycle
 */
@Composable
fun ScreenLifecycleEffect(viewModel: ScreenLifecycle) {
    ScreenLifecycleEffect(
        key = viewModel,
        onCreate = viewModel::onCreated,
        onResume = viewModel::onResumed,
        onPause = viewModel::onPaused
    )
}

/**
 * Collects navigation events from a Flow and performs actual navigation.
 * Caller is responsible for handling specific event types via when expression.
 */
@Composable
fun CollectNavEvents(
    navEventFlow: Flow<NavEvent>,
    onEvent: (NavEvent) -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(navEventFlow) {
        navEventFlow.collect { event ->
            currentOnEvent(event)
        }
    }
}
