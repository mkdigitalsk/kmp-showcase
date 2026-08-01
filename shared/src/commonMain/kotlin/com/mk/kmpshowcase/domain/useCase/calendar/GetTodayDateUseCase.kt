package com.mk.kmpshowcase.domain.useCase.calendar

import kotlinx.datetime.LocalDate
import com.mk.kmpshowcase.domain.repository.DateRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class GetTodayDateUseCase(
    private val dateRepository: DateRepository,
) : UseCase<None, LocalDate>() {
    override suspend fun run(params: None): LocalDate = dateRepository.today()
}
