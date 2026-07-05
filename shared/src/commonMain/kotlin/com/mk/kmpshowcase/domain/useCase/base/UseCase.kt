package com.mk.kmpshowcase.domain.useCase.base

abstract class UseCase<in Params, out Result> {

    protected abstract suspend fun run(params: Params): Result

    // Main-safety is the repository's responsibility (see NoteRepositoryImpl); use cases stay
    // dispatcher-agnostic and delegate. Keeps use cases pure and VM tests deterministic.
    suspend operator fun invoke(params: Params): Result = run(params)
}

suspend operator fun <T> UseCase<None, T>.invoke(): T = invoke(None)

object None
