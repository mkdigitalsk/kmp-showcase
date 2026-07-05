package com.mk.kmpshowcase.presentation.screen.platformapis

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Job
import com.mk.kmpshowcase.domain.useCase.base.invoke
import com.mk.kmpshowcase.domain.useCase.biometric.AuthenticateWithBiometricUseCase
import com.mk.kmpshowcase.domain.useCase.biometric.IsBiometricEnabledUseCase
import com.mk.kmpshowcase.domain.useCase.flashlight.IsFlashlightAvailableUseCase
import com.mk.kmpshowcase.domain.useCase.flashlight.ToggleFlashlightUseCase
import com.mk.kmpshowcase.domain.useCase.flashlight.TurnOffFlashlightUseCase
import com.mk.kmpshowcase.domain.useCase.location.GetLastKnownLocationUseCase
import com.mk.kmpshowcase.domain.useCase.location.ObserveLocationUpdatesUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModel
import com.mk.kmpshowcase.presentation.base.NavEvent

@Suppress("LongParameterList", "TooManyFunctions")
class PlatformApisViewModel(
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val observeLocationUpdatesUseCase: ObserveLocationUpdatesUseCase,
    private val isBiometricEnabledUseCase: IsBiometricEnabledUseCase,
    private val authenticateWithBiometricUseCase: AuthenticateWithBiometricUseCase,
    private val isFlashlightAvailableUseCase: IsFlashlightAvailableUseCase,
    private val toggleFlashlightUseCase: ToggleFlashlightUseCase,
    private val turnOffFlashlightUseCase: TurnOffFlashlightUseCase,
) : BaseViewModel<PlatformApisUiState>(PlatformApisUiState()) {

    private var locationUpdatesJob: Job? = null

    override fun loadInitialData() {
        execute(
            action = {
                Pair(
                    isBiometricEnabledUseCase(),
                    isFlashlightAvailableUseCase()
                )
            },
            onSuccess = { (biometricsAvailable, flashlightAvailable) ->
                newState {
                    it.copy(
                        biometricsAvailable = biometricsAvailable,
                        flashlightAvailable = flashlightAvailable
                    )
                }
            }
        )
    }

    fun toggleFlashlight() {
        execute(
            action = { toggleFlashlightUseCase() },
            onSuccess = { success ->
                if (success) {
                    newState { it.copy(flashlightOn = !it.flashlightOn) }
                }
            }
        )
    }

    fun share(text: String) {
        navigate(PlatformApisNavEvent.Share(text))
    }

    fun dial() {
        navigate(PlatformApisNavEvent.Dial(DEMO_PHONE_NUMBER))
    }

    fun openLink() {
        navigate(PlatformApisNavEvent.OpenLink(DEMO_URL))
    }

    fun sendEmail(subject: String, body: String) {
        navigate(PlatformApisNavEvent.SendEmail(DEMO_EMAIL, subject, body))
    }

    fun copyToClipboard(text: String) {
        navigate(PlatformApisNavEvent.CopyToClipboard(text))
        newState { it.copy(copiedToClipboard = true) }
    }

    fun resetCopyState() {
        newState { it.copy(copiedToClipboard = false) }
    }

    fun getLocation() {
        execute(
            action = { getLastKnownLocationUseCase() },
            onLoading = { newState { it.copy(locationLoading = true, locationError = false) } },
            onSuccess = { location ->
                newState { it.copy(location = location.toUiModel(), locationLoading = false) }
            },
            onError = {
                newState { it.copy(locationLoading = false, locationError = true) }
            }
        )
    }

    override fun onResumed() {
        super.onResumed()
        requireState { state -> if (state.shouldTrackLocation) startLocationUpdates() }
    }

    override fun onPaused() {
        super.onPaused()
        requireState { currentState -> newState { it.copy(shouldTrackLocation = currentState.isTrackingLocation) } }
        stopLocationUpdates()
    }

    fun startLocationUpdates() {
        if (locationUpdatesJob?.isActive == true) return
        newState { it.copy(isTrackingLocation = true, locationUpdatesError = false) }
        locationUpdatesJob = observe(
            flow = observeLocationUpdatesUseCase(ObserveLocationUpdatesUseCase.Params(highAccuracy = true)),
            onEach = { location -> newState { it.copy(trackedLocation = location.toUiModel()) } },
            onError = { newState { it.copy(isTrackingLocation = false, locationUpdatesError = true) } }
        )
    }

    fun stopLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
        newState { it.copy(isTrackingLocation = false) }
    }

    fun authenticateWithBiometrics() {
        execute(
            action = { authenticateWithBiometricUseCase() },
            onLoading = { newState { it.copy(biometricsLoading = true, biometricsResult = null) } },
            onSuccess = { result ->
                newState { it.copy(biometricsLoading = false, biometricsResult = result.toUiModel()) }
            },
            onError = { error ->
                val result = BiometricUiModel(BiometricUiStatus.FAILED, error.message?.takeIf { it.isNotBlank() })
                newState { it.copy(biometricsLoading = false, biometricsResult = result) }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
        if (state.value.flashlightOn) {
            execute(action = { turnOffFlashlightUseCase() })
        }
    }

    private companion object {
        private const val DEMO_PHONE_NUMBER = "+1234567890"
        private const val DEMO_URL = "https://github.com/KusnirM"
        private const val DEMO_EMAIL = "example@example.com"
    }
}

@Immutable
data class PlatformApisUiState(
    val copiedToClipboard: Boolean = false,
    val location: LocationUiModel? = null,
    val locationLoading: Boolean = false,
    val locationError: Boolean = false,
    val isTrackingLocation: Boolean = false,
    val shouldTrackLocation: Boolean = false,
    val trackedLocation: LocationUiModel? = null,
    val locationUpdatesError: Boolean = false,
    val biometricsAvailable: Boolean = false,
    val biometricsLoading: Boolean = false,
    val biometricsResult: BiometricUiModel? = null,
    val flashlightAvailable: Boolean = false,
    val flashlightOn: Boolean = false,
)

sealed interface PlatformApisNavEvent : NavEvent {
    data class Share(val text: String) : PlatformApisNavEvent
    data class Dial(val number: String) : PlatformApisNavEvent
    data class OpenLink(val url: String) : PlatformApisNavEvent
    data class SendEmail(val to: String, val subject: String, val body: String) : PlatformApisNavEvent
    data class CopyToClipboard(val text: String) : PlatformApisNavEvent
}
