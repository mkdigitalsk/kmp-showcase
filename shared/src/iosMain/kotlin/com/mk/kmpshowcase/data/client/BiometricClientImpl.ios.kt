package com.mk.kmpshowcase.data.client

import com.mk.kmpshowcase.domain.model.BiometricResult
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorAuthenticationFailed
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorUserFallback
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class BiometricClientImpl : BiometricClient {

    actual override fun enabled(): Boolean {
        val context = LAContext()
        return context.canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            error = null
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun authenticate(): BiometricResult {
        if (!enabled()) {
            return BiometricResult.NotAvailable
        }

        return suspendCoroutine { continuation ->
            val context = LAContext()

            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = "Authenticate to continue"
            ) { success, error ->
                val result = when {
                    success -> BiometricResult.Success
                    error != null -> mapError(error)
                    else -> BiometricResult.SystemError("")
                }
                continuation.resume(result)
            }
        }
    }

    private fun mapError(error: NSError): BiometricResult {
        return when (error.code) {
            LAErrorUserCancel, LAErrorUserFallback -> BiometricResult.Cancelled
            LAErrorAuthenticationFailed -> BiometricResult.SystemError(error.localizedDescription)
            else -> BiometricResult.SystemError(error.localizedDescription)
        }
    }
}
