package mk.digital.kmpshowcase.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import mk.digital.kmpshowcase.domain.exceptions.base.BaseException
import mk.digital.kmpshowcase.domain.exceptions.base.UnknownException
import mk.digital.kmpshowcase.util.Logger

/**
 * Screen lifecycle callbacks.
 */
interface ScreenLifecycle {
    fun onCreated() {}
    fun onResumed() {}
    fun onPaused() {}
}

/**
 * Base ViewModel for all screens.
 * Provides state management, navigation, error handling, and lifecycle.
 */
abstract class BaseViewModel<STATE : Any>(
    defaultState: STATE,
) : ViewModel(), ScreenLifecycle {

    protected val tag = this::class.simpleName
    private var isInitialized = false

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(defaultState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _navEvent = MutableSharedFlow<NavEvent>()
    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    private val scope get() = viewModelScope

    /**
     * Called to load initial data. Override in subclasses.
     * Only called once per ViewModel lifecycle.
     */
    protected open fun loadInitialData() {
        // Default implementation does nothing
    }

    /**
     * Called when the screen is first created.
     */
    override fun onCreated() {
        if (!isInitialized) {
            isInitialized = true
            loadInitialData()
            logScreenName()
        }
    }

    /**
     * Called when the screen is resumed/becomes visible.
     */
    override fun onResumed() {}

    /**
     * Called when the screen is paused/becomes invisible.
     */
    override fun onPaused() {}

    /**
     * Emits a navigation event to be handled by the UI layer.
     */
    protected fun navigate(event: NavEvent) {
        viewModelScope.launch { _navEvent.emit(event) }
    }

    /**
     * Updates the state using a copy function.
     */
    protected fun newState(stateCopy: (STATE) -> STATE) {
        _state.value = stateCopy(_state.value)
    }

    /**
     * Executes a block with the current state.
     */
    protected fun requireState(block: (STATE) -> Unit): Unit = block(_state.value)

    /**
     * Returns the current state value.
     */
    protected fun requireState(): STATE = _state.value

    /**
     * Logs the screen name for analytics.
     */
    protected fun logScreenName() {
        Logger.d("Screen: ${tag?.removeSuffix("ViewModel")}")
    }

    /**
     * Executes a suspending action with standardized error handling.
     *
     * @param action The suspend function to execute
     * @param onLoading Called before action starts (use to show loading state)
     * @param onSuccess Called with the result when action succeeds
     * @param onError Called with BaseException when action fails
     * @return Job that can be used to cancel the operation
     */
    protected fun <T> execute(
        action: suspend () -> T,
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (BaseException) -> Unit = {}
    ): Job = scope.launch {
        onLoading()
        try {
            onSuccess(action())
        } catch (e: BaseException) {
            Logger.e("${tag}: ${e.message}", e)
            onError(e)
        } catch (e: Throwable) {
            Logger.e("${tag}: ${e.message}", e)
            onError(UnknownException(e))
        }
    }

    /**
     * Observes a Flow with standardized error handling.
     *
     * @param onStart Optional suspend action executed before collection (e.g., load initial data)
     * @param flow The flow to observe
     * @param onEach Called for each emission
     * @param onError Called with BaseException when flow or onStart errors
     * @return Job that can be used to cancel the observation
     */
    protected fun <T> observe(
        onStart: (suspend () -> Unit)? = null,
        flow: Flow<T>,
        onEach: (T) -> Unit,
        onError: (BaseException) -> Unit = {}
    ): Job = scope.launch {
        try {
            onStart?.invoke()
        } catch (e: BaseException) {
            Logger.e("${tag}: ${e.message}", e)
            onError(e)
            return@launch
        } catch (e: Throwable) {
            Logger.e("${tag}: ${e.message}", e)
            onError(UnknownException(e))
            return@launch
        }
        flow.catch { e ->
            when (e) {
                is BaseException -> {
                    Logger.e("${tag}: ${e.message}", e)
                    onError(e)
                }
                else -> {
                    Logger.e("${tag}: ${e.message}", e)
                    onError(UnknownException(e))
                }
            }
        }.collect { onEach(it) }
    }
}

interface NavEvent
