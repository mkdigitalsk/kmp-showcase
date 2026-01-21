package mk.digital.kmpshowcase.domain.exceptions.base

/**
 * Error codes:
 * - 1xxx: Network errors (1001 = no connection, 1002 = timeout)
 * - 2-xxx: API errors - combined with HTTP code (2-401, 2-404, 2-500)
 * - 3xxx: Data/parsing errors (3001 = parsing, 3002 = serialization)
 * - 9xxx: Unknown errors (9000)
 */

/**
 * Base exception class for all domain-level exceptions.
 * Extend this class to create specific exception types.
 */
abstract class BaseException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Error code for identification and support.
     * - API errors: "2-{httpCode}" (e.g., "2-401")
     * - Other errors: simple code (e.g., "1001", "3001")
     */
    abstract val errorCode: String

    /**
     * User-friendly message that can be displayed in the UI.
     */
    abstract val userMessage: String

    /**
     * Whether this exception should be logged/reported to crash analytics.
     */
    open val shouldReport: Boolean = true
}

/**
 * Network error codes (1xxx).
 */
object NetworkErrorCode {
    const val UNKNOWN = "1000"
    const val NO_CONNECTION = "1001"
    const val TIMEOUT = "1002"
}

/**
 * Exception for network-related errors.
 */
class NetworkException(
    message: String = "Network error occurred",
    cause: Throwable? = null,
    override val userMessage: String = "Please check your internet connection",
    override val errorCode: String = NetworkErrorCode.UNKNOWN
) : BaseException(message, cause)

/**
 * Exception for API/server errors.
 * Error code format: "2-{httpStatusCode}" (e.g., "2-401", "2-404", "2-500")
 */
class ApiException(
    val httpCode: Int,
    message: String,
    cause: Throwable? = null,
    override val userMessage: String = "Something went wrong. Please try again."
) : BaseException(message, cause) {
    override val errorCode: String = "2-$httpCode"
}

/**
 * Data error codes (3xxx).
 */
object DataErrorCode {
    const val UNKNOWN = "3000"
    const val PARSING = "3001"
    const val SERIALIZATION = "3002"
}

/**
 * Exception for data parsing/transformation errors.
 */
class DataException(
    message: String = "Data parsing error",
    cause: Throwable? = null,
    override val userMessage: String = "Unable to process data",
    override val errorCode: String = DataErrorCode.UNKNOWN
) : BaseException(message, cause)

/**
 * Generic unknown exception wrapper.
 */
class UnknownException(
    cause: Throwable? = null,
    override val userMessage: String = "An unexpected error occurred",
    override val errorCode: String = "9000"
) : BaseException(cause?.message ?: "Unknown error", cause)
