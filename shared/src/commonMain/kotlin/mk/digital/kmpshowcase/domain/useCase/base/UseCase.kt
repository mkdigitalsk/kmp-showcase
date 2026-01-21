package mk.digital.kmpshowcase.domain.useCase.base

/**
 * Base class for all use cases.
 * Encapsulates a single business logic operation.
 *
 * @param Params Input parameters for the use case
 * @param Result Output result from the use case
 */
abstract class UseCase<in Params, out Result> {

    /**
     * Executes the use case logic.
     * Override this in concrete implementations.
     */
    protected abstract suspend fun run(params: Params): Result

    /**
     * Invokes the use case.
     */
    suspend operator fun invoke(params: Params): Result = run(params)
}

/**
 * Extension for use cases that don't require parameters.
 */
suspend operator fun <T> UseCase<None, T>.invoke(): T = invoke(None)

/**
 * Marker object for use cases without parameters.
 */
object None
