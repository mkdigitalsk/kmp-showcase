package mk.digital.kmpshowcase.domain.useCase.base

import kotlinx.coroutines.flow.Flow

/**
 * Base class for use cases that return a Flow.
 * Used for observing data streams.
 *
 * @param Params Input parameters for the use case
 * @param Result Output result type emitted by the Flow
 */
abstract class FlowUseCase<in Params, out Result> {

    /**
     * Creates the Flow.
     * Override this in concrete implementations.
     */
    protected abstract fun run(params: Params): Flow<Result>

    /**
     * Invokes the use case.
     */
    operator fun invoke(params: Params): Flow<Result> = run(params)
}

/**
 * Extension for flow use cases that don't require parameters.
 */
operator fun <T> FlowUseCase<None, T>.invoke(): Flow<T> = invoke(None)
