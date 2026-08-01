package com.mk.kmpshowcase.presentation.base

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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlinx.coroutines.flow.Flow
import org.koin.compose.currentKoinScope
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.viewmodel.defaultExtras

@Composable
fun ScreenLifecycleEffect(
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

    var isResumed by remember(key) { mutableStateOf(false) }
    var isFirstComposition by remember(key) { mutableStateOf(true) }

    LaunchedEffect(key) {
        if (isFirstComposition) {
            isFirstComposition = false
            currentOnCreate()
        }

        if (!isResumed && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            isResumed = true
            currentOnResume()
        }
    }

    DisposableEffect(key) {
        onDispose {
            if (isResumed) {
                isResumed = false
                currentOnPause()
            }
            currentOnDispose()
        }
    }

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
 * A lifecycle-aware version of hiltViewModel that automatically connects
 * ViewModel's onResumed/onPaused to the composable's lifecycle.
 */
@Composable
inline fun <reified VM : BaseViewModel<*>> lifecycleAwareViewModel(
    qualifier: Qualifier? = null,
    viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"),
    key: String? = null,
    extras: CreationExtras = defaultExtras(viewModelStoreOwner),
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): VM {
    val viewModel = koinViewModel<VM>(
        qualifier = qualifier,
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        extras = extras,
        scope = scope,
        parameters = parameters
    )
    ScreenLifecycleEffect(
        key = viewModel,
        onCreate = viewModel::onCreated,
        onResume = viewModel::onResumed,
        onPause = viewModel::onPaused
    )
    return viewModel
}

@Composable
fun <T : NavEvent> CollectNavEvents(
    navEventFlow: Flow<T>,
    onEvent: (T) -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(navEventFlow) {
        navEventFlow.collect { event ->
            currentOnEvent(event)
        }
    }
}
