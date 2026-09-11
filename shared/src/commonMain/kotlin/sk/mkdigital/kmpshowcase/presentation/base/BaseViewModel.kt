package sk.mkdigital.kmpshowcase.presentation.base

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
import sk.mkdigital.kmpshowcase.domain.exceptions.base.BaseException
import sk.mkdigital.kmpshowcase.domain.exceptions.base.UnknownException
import sk.mkdigital.kmpshowcase.util.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sk.mkdigital.kmpshowcase.util.suspendRunCatching

interface ScreenLifecycle {
    fun onCreated() {}
    fun onResumed() {}
    fun onPaused() {}
}

@Suppress("AbstractClassCanBeConcreteClass")
abstract class BaseViewModel<STATE : Any>(
    defaultState: STATE,
) : ViewModel(), ScreenLifecycle, KoinComponent {

    private val logger: Logger by inject()

    protected val tag = this::class.simpleName
    private var isCreated = false

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(defaultState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _navEvent = MutableSharedFlow<NavEvent>()
    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    private val scope get() = viewModelScope

    protected open fun loadInitialData() {}


    final override fun onCreated() {
        if (isCreated) return
        isCreated = true
        onCreate()
    }

    /**
     * Once per screen, as an activity's is.
     *
     * ⚠ The lifecycle signals creation on every entry into composition, and a nav-back is one, so
     * the guard above is what makes this a one-off. Resuming and pausing then carry the rest, alike
     * on both platforms: a navigation away and back, and the app losing and regaining focus —
     * backgrounded, or covered by a dialog, which Compose Multiplatform raises on iOS from
     * `willResignActive` and `didBecomeActive`.
     */
    protected open fun onCreate() {
        loadInitialData()
        logScreenName()
    }

    override fun onResumed() {}

    override fun onPaused() {}

    protected fun navigate(event: NavEvent) {
        viewModelScope.launch { _navEvent.emit(event) }
    }

    protected fun newState(stateCopy: (STATE) -> STATE) {
        _state.value = stateCopy(_state.value)
    }

    protected fun requireState(block: (STATE) -> Unit): Unit = block(_state.value)

    protected fun requireState(): STATE = _state.value

    protected fun logScreenName() {
        val screenName = tag?.removeSuffix("ViewModel") ?: return
        logger.d("Screen: $screenName")
    }

    protected fun <T> execute(
        action: suspend () -> T,
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (BaseException) -> Unit = {}
    ): Job = scope.launch {
        onLoading()
        suspendRunCatching { action() }
            .onSuccess(onSuccess)
            .onFailure { report(it, onError) }
    }

    private fun report(error: Throwable, onError: (BaseException) -> Unit) {
        logger.e("${tag}: ${error.message}", error)
        onError(error as? BaseException ?: UnknownException(error))
    }

    protected fun <T> observe(
        onStart: (suspend () -> Unit)? = null,
        flow: Flow<T>,
        onEach: (T) -> Unit,
        onError: (BaseException) -> Unit = {}
    ): Job = scope.launch {
        suspendRunCatching { onStart?.invoke() }
            .onFailure {
                report(it, onError)
                return@launch
            }
        flow.catch { e ->
            when (e) {
                is BaseException -> {
                    logger.e("${tag}: ${e.message}", e)
                    onError(e)
                }
                else -> {
                    logger.e("${tag}: ${e.message}", e)
                    onError(UnknownException(e))
                }
            }
        }.collect { onEach(it) }
    }
}

interface NavEvent
