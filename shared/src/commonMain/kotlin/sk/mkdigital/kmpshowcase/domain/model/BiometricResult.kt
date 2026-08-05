package sk.mkdigital.kmpshowcase.domain.model

sealed interface BiometricResult {
    data object Success : BiometricResult
    data object Cancelled : BiometricResult
    data object NotAvailable : BiometricResult
    data object ActivityNotAvailable : BiometricResult
    data class SystemError(val message: String) : BiometricResult
}
