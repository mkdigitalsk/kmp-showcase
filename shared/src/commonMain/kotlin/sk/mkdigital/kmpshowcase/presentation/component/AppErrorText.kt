package sk.mkdigital.kmpshowcase.presentation.component

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import sk.mkdigital.kmpshowcase.presentation.base.AppError
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_data
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_generic
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_location
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_no_connection
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_not_found
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_server
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_timeout
import sk.mkdigital.kmpshowcase.shared.generated.resources.error_unauthorized

@Composable
fun AppError.text(): String = stringResource(
    when (this) {
        AppError.NO_CONNECTION -> Res.string.error_no_connection
        AppError.TIMEOUT -> Res.string.error_timeout
        AppError.UNAUTHORIZED -> Res.string.error_unauthorized
        AppError.NOT_FOUND -> Res.string.error_not_found
        AppError.SERVER -> Res.string.error_server
        AppError.DATA -> Res.string.error_data
        AppError.LOCATION -> Res.string.error_location
        AppError.GENERIC -> Res.string.error_generic
    }
)
