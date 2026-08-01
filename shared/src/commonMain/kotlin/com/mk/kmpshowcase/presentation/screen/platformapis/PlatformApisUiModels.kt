package com.mk.kmpshowcase.presentation.screen.platformapis

import androidx.compose.runtime.Immutable
import com.mk.kmpshowcase.domain.model.BiometricResult
import com.mk.kmpshowcase.domain.model.Location
import com.mk.kmpshowcase.util.StringFormatter

private const val COORDINATE_DECIMAL_PLACES = 6

enum class BiometricUiStatus { SUCCESS, FAILED, CANCELLED, NOT_AVAILABLE, ACTIVITY_NOT_AVAILABLE }

@Immutable
data class BiometricUiModel(
    val status: BiometricUiStatus,
    val errorDetail: String? = null,
)

@Immutable
data class LocationUiModel(
    val latitude: String,
    val longitude: String,
)

fun BiometricResult.toUiModel(): BiometricUiModel = when (this) {
    is BiometricResult.Success -> BiometricUiModel(BiometricUiStatus.SUCCESS)
    is BiometricResult.SystemError -> BiometricUiModel(BiometricUiStatus.FAILED, message.takeIf { it.isNotBlank() })
    is BiometricResult.Cancelled -> BiometricUiModel(BiometricUiStatus.CANCELLED)
    is BiometricResult.NotAvailable -> BiometricUiModel(BiometricUiStatus.NOT_AVAILABLE)
    is BiometricResult.ActivityNotAvailable -> BiometricUiModel(BiometricUiStatus.ACTIVITY_NOT_AVAILABLE)
}

fun Location.toUiModel(): LocationUiModel = LocationUiModel(
    latitude = StringFormatter.formatDouble(lat, COORDINATE_DECIMAL_PLACES),
    longitude = StringFormatter.formatDouble(lon, COORDINATE_DECIMAL_PLACES),
)
