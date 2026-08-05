package sk.mkdigital.kmpshowcase.domain.useCase.base

import sk.mkdigital.kmpshowcase.util.Mockable
import kotlinx.coroutines.flow.Flow

@Mockable
abstract class FlowUseCase<in Params, out Result> {

    protected abstract fun run(params: Params): Flow<Result>

    operator fun invoke(params: Params): Flow<Result> = run(params)
}

operator fun <T> FlowUseCase<None, T>.invoke(): Flow<T> = invoke(None)
