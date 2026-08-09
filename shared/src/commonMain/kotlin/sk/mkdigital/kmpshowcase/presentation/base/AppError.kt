package sk.mkdigital.kmpshowcase.presentation.base

import sk.mkdigital.kmpshowcase.domain.exceptions.base.ApiException
import sk.mkdigital.kmpshowcase.domain.exceptions.base.BaseException
import sk.mkdigital.kmpshowcase.domain.exceptions.base.DataException
import sk.mkdigital.kmpshowcase.domain.exceptions.base.LocationException
import sk.mkdigital.kmpshowcase.domain.exceptions.base.NetworkErrorCode
import sk.mkdigital.kmpshowcase.domain.exceptions.base.NetworkException

/**
 * What a failure means to the person looking at the screen. A ViewModel maps a domain exception to
 * one of these; the Composable turns it into text, so no string is resolved outside the UI.
 */
enum class AppError {
    NO_CONNECTION,
    TIMEOUT,
    UNAUTHORIZED,
    NOT_FOUND,
    SERVER,
    DATA,
    LOCATION,
    GENERIC,
}

/**
 * Collapsing to [AppError.GENERIC] is the intended outcome for anything the user cannot act on
 * differently — the alternative is a case per HTTP status that all say the same sentence.
 */
fun BaseException.toAppError(): AppError = when (this) {
    is NetworkException -> when (errorCode) {
        NetworkErrorCode.NO_CONNECTION -> AppError.NO_CONNECTION
        NetworkErrorCode.TIMEOUT -> AppError.TIMEOUT
        else -> AppError.GENERIC
    }

    is ApiException -> when (httpCode) {
        HTTP_UNAUTHORIZED -> AppError.UNAUTHORIZED
        HTTP_NOT_FOUND -> AppError.NOT_FOUND
        in HTTP_SERVER_ERROR_RANGE -> AppError.SERVER
        else -> AppError.GENERIC
    }

    is DataException -> AppError.DATA
    is LocationException -> AppError.LOCATION
    else -> AppError.GENERIC
}

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_NOT_FOUND = 404
private val HTTP_SERVER_ERROR_RANGE = 500..599
