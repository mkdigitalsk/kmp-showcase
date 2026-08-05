package sk.mkdigital.kmpshowcase.presentation.screen.storage

import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import sk.mkdigital.kmpshowcase.domain.useCase.storage.ClearCacheUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.LoadStorageDataUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.ObserveStorageDataUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.SetPersistentCounterUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.storage.SetSessionCounterUseCase
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel

class StorageViewModel(
    private val loadStorageDataUseCase: LoadStorageDataUseCase,
    private val observeStorageDataUseCase: ObserveStorageDataUseCase,
    private val setSessionCounterUseCase: SetSessionCounterUseCase,
    private val setPersistentCounterUseCase: SetPersistentCounterUseCase,
    private val clearCacheUseCase: ClearCacheUseCase
) : BaseViewModel<StorageUiState>(StorageUiState()) {

    override fun loadInitialData() {
        observe(
            onStart = { loadStorageDataUseCase() },
            flow = observeStorageDataUseCase(),
            onEach = { data ->
                newState {
                    it.copy(
                        sessionCounter = data.sessionCounter,
                        persistentCounter = data.persistentCounter
                    )
                }
            }
        )
    }

    fun incrementSessionCounter() {
        val newValue = state.value.sessionCounter + 1
        execute(action = { setSessionCounterUseCase(newValue) })
    }

    fun decrementSessionCounter() {
        val newValue = (state.value.sessionCounter - 1).coerceAtLeast(0)
        execute(action = { setSessionCounterUseCase(newValue) })
    }

    fun incrementPersistentCounter() {
        val newValue = state.value.persistentCounter + 1
        execute(action = { setPersistentCounterUseCase(newValue) })
    }

    fun decrementPersistentCounter() {
        val newValue = (state.value.persistentCounter - 1).coerceAtLeast(0)
        execute(action = { setPersistentCounterUseCase(newValue) })
    }

    fun clearSession() {
        execute(action = { clearCacheUseCase() })
    }
}

data class StorageUiState(
    val sessionCounter: Int = 0,
    val persistentCounter: Int = 0
)
