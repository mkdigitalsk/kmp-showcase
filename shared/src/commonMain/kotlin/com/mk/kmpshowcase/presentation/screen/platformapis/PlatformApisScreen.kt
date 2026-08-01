package com.mk.kmpshowcase.presentation.screen.platformapis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import com.mk.kmpshowcase.LocalSnackbarHostState
import com.mk.kmpshowcase.presentation.base.CollectNavEvents
import com.mk.kmpshowcase.presentation.base.NavEvent
import com.mk.kmpshowcase.presentation.base.NavRouter
import com.mk.kmpshowcase.presentation.base.Route
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.buttons.OutlinedButton
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.permission.rememberLocationPermissionState
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_activity_not_available
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_cancelled
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_failed
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_not_available
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_success
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_biometrics_unknown_error
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_copied_message
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_flashlight_not_available
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_flashlight_off
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_flashlight_on
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_flashlight_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_copy_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_copy_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_demo_copy_text
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_demo_email_body
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_demo_email_subject
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_demo_share_text
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_dial_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_dial_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_email_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_email_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_link_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_link_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_error
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_loading
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_result
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_updates_error
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_updates_start
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_updates_stop
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_location_updates_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_share_action
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_share_title
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_subtitle
import com.mk.kmpshowcase.shared.generated.resources.platform_apis_title
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@Suppress("CyclomaticComplexMethod", "CognitiveComplexMethod")
@Composable
fun PlatformApisScreen(
    router: NavRouter<Route>,
    viewModel: PlatformApisViewModel = lifecycleAwareViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val copiedMessage = stringResource(Res.string.platform_apis_copied_message)

    val handleLocationAction = rememberLocationActionHandler(
        onGetLocation = viewModel::getLocation,
        onStartUpdates = viewModel::startLocationUpdates
    )

    LaunchedEffect(state.copiedToClipboard) {
        if (state.copiedToClipboard) {
            snackbarHostState.showSnackbar(copiedMessage)
            delay(100.milliseconds)
            viewModel.resetCopyState()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space4,
            end = space4,
            top = space4,
            bottom = floatingNavBarSpace
        ),
        verticalArrangement = Arrangement.spacedBy(space4)
    ) {
        item {
            Column {
                TextHeadlineMediumPrimary(stringResource(Res.string.platform_apis_title))
                TextBodyMediumNeutral80(stringResource(Res.string.platform_apis_subtitle))
            }
        }

        item {
            val shareText = stringResource(Res.string.platform_apis_demo_share_text)
            PlatformApiCard(
                icon = Icons.Outlined.Share,
                title = stringResource(Res.string.platform_apis_share_title)
            ) {
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_share_action),
                    onClick = { viewModel.share(shareText) }
                )
            }
        }

        item {
            PlatformApiCard(
                icon = Icons.Outlined.Phone,
                title = stringResource(Res.string.platform_apis_dial_title)
            ) {
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_dial_action),
                    onClick = viewModel::dial
                )
            }
        }

        item {
            PlatformApiCard(
                icon = Icons.Outlined.Link,
                title = stringResource(Res.string.platform_apis_link_title)
            ) {
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_link_action),
                    onClick = viewModel::openLink
                )
            }
        }

        item {
            val emailSubject = stringResource(Res.string.platform_apis_demo_email_subject)
            val emailBody = stringResource(Res.string.platform_apis_demo_email_body)
            PlatformApiCard(
                icon = Icons.Outlined.Email,
                title = stringResource(Res.string.platform_apis_email_title)
            ) {
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_email_action),
                    onClick = { viewModel.sendEmail(emailSubject, emailBody) }
                )
            }
        }

        item {
            val copyText = stringResource(Res.string.platform_apis_demo_copy_text)
            PlatformApiCard(
                icon = Icons.Outlined.ContentCopy,
                title = stringResource(Res.string.platform_apis_copy_title)
            ) {
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_copy_action),
                    onClick = { viewModel.copyToClipboard(copyText) }
                )
            }
        }

        item {
            val loadingText = stringResource(Res.string.platform_apis_location_loading)
            val errorText = stringResource(Res.string.platform_apis_location_error)
            val location = state.location
            PlatformApiCard(
                icon = Icons.Outlined.LocationOn,
                title = stringResource(Res.string.platform_apis_location_title)
            ) {
                val locationText = when {
                    state.locationLoading -> loadingText
                    state.locationError -> errorText
                    location != null ->
                        stringResource(Res.string.platform_apis_location_result, location.latitude, location.longitude)
                    else -> null
                }
                locationText?.let { TextBodyMediumNeutral80(it) }
                Spacer2()
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_location_action),
                    onClick = { handleLocationAction(PendingLocationAction.GET_LOCATION) }
                )
            }
        }

        item {
            val errorText = stringResource(Res.string.platform_apis_location_updates_error)
            val trackedLocation = state.trackedLocation
            PlatformApiCard(
                icon = Icons.Outlined.MyLocation,
                title = stringResource(Res.string.platform_apis_location_updates_title)
            ) {
                val trackedText = when {
                    state.locationUpdatesError -> errorText
                    trackedLocation != null -> stringResource(
                        Res.string.platform_apis_location_result,
                        trackedLocation.latitude,
                        trackedLocation.longitude
                    )
                    else -> null
                }
                trackedText?.let { TextBodyMediumNeutral80(it) }
                Spacer2()
                ApiCardButton(
                    text = stringResource(
                        if (state.isTrackingLocation) Res.string.platform_apis_location_updates_stop
                        else Res.string.platform_apis_location_updates_start
                    ),
                    onClick = {
                        if (state.isTrackingLocation) viewModel.stopLocationUpdates()
                        else handleLocationAction(PendingLocationAction.START_UPDATES)
                    }
                )
            }
        }

        item {
            val successText = stringResource(Res.string.platform_apis_biometrics_success)
            val failedText = stringResource(Res.string.platform_apis_biometrics_failed)
            val cancelledText = stringResource(Res.string.platform_apis_biometrics_cancelled)
            val notAvailableText = stringResource(Res.string.platform_apis_biometrics_not_available)
            val activityNotAvailableText = stringResource(Res.string.platform_apis_biometrics_activity_not_available)
            val unknownErrorText = stringResource(Res.string.platform_apis_biometrics_unknown_error)
            val biometric = state.biometricsResult
            PlatformApiCard(
                icon = Icons.Outlined.Fingerprint,
                title = stringResource(Res.string.platform_apis_biometrics_title)
            ) {
                val biometricText = when {
                    !state.biometricsAvailable -> notAvailableText
                    state.biometricsLoading -> "..."
                    biometric != null -> when (biometric.status) {
                        BiometricUiStatus.SUCCESS -> successText
                        BiometricUiStatus.FAILED -> "$failedText: ${biometric.errorDetail ?: unknownErrorText}"
                        BiometricUiStatus.CANCELLED -> cancelledText
                        BiometricUiStatus.NOT_AVAILABLE -> notAvailableText
                        BiometricUiStatus.ACTIVITY_NOT_AVAILABLE -> activityNotAvailableText
                    }
                    else -> null
                }
                biometricText?.let { TextBodyMediumNeutral80(it) }
                Spacer2()
                ApiCardButton(
                    text = stringResource(Res.string.platform_apis_biometrics_action),
                    onClick = viewModel::authenticateWithBiometrics,
                    enabled = state.biometricsAvailable && !state.biometricsLoading
                )
            }
        }

        item {
            val notAvailableText = stringResource(Res.string.platform_apis_flashlight_not_available)
            PlatformApiCard(
                icon = Icons.Outlined.FlashlightOn,
                title = stringResource(Res.string.platform_apis_flashlight_title)
            ) {
                if (!state.flashlightAvailable) {
                    TextBodyMediumNeutral80(notAvailableText)
                }
                Spacer2()
                ApiCardButton(
                    text = stringResource(
                        if (state.flashlightOn) Res.string.platform_apis_flashlight_off
                        else Res.string.platform_apis_flashlight_on
                    ),
                    onClick = viewModel::toggleFlashlight,
                    enabled = state.flashlightAvailable
                )
            }
        }
    }

    PlatformApisNavEvents(router = router, viewModel.navEvent)
}

@Composable
private fun PlatformApiCard(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(space4))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
                content = {
                    TextBodyLargeNeutral80(title)
                    Spacer2()
                    content()
                }
            )
        }
    }
}

@Composable
private fun ApiCardButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedButton(
        text = text,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
}


@Composable
private fun PlatformApisNavEvents(
    router: NavRouter<Route>,
    navEvent: SharedFlow<NavEvent>,
) {
    CollectNavEvents(navEventFlow = navEvent) { event ->
        when (event) {
            is PlatformApisNavEvent.Share -> router.share(event.text)
            is PlatformApisNavEvent.Dial -> router.dial(event.number)
            is PlatformApisNavEvent.OpenLink -> router.openLink(event.url)
            is PlatformApisNavEvent.SendEmail -> router.sendEmail(event.to, event.subject, event.body)
            is PlatformApisNavEvent.CopyToClipboard -> router.copyToClipboard(event.text)
        }
    }
}

private enum class PendingLocationAction { NONE, GET_LOCATION, START_UPDATES }

@Composable
private fun rememberLocationActionHandler(
    onGetLocation: () -> Unit,
    onStartUpdates: () -> Unit
): (PendingLocationAction) -> Unit {
    val locationPermission = rememberLocationPermissionState()
    var pendingAction by remember { mutableStateOf(PendingLocationAction.NONE) }

    LaunchedEffect(locationPermission.isGranted, pendingAction) {
        if (locationPermission.isGranted && pendingAction != PendingLocationAction.NONE) {
            when (pendingAction) {
                PendingLocationAction.GET_LOCATION -> onGetLocation()
                PendingLocationAction.START_UPDATES -> onStartUpdates()
                PendingLocationAction.NONE -> Unit
            }
            pendingAction = PendingLocationAction.NONE
        }
    }

    return { action ->
        if (locationPermission.isGranted) {
            when (action) {
                PendingLocationAction.GET_LOCATION -> onGetLocation()
                PendingLocationAction.START_UPDATES -> onStartUpdates()
                PendingLocationAction.NONE -> Unit
            }
        } else {
            pendingAction = action
            locationPermission.requestPermission()
        }
    }
}
