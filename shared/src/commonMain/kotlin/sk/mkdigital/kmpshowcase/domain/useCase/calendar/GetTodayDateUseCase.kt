package sk.mkdigital.kmpshowcase.domain.useCase.calendar

import kotlinx.datetime.LocalDate
import sk.mkdigital.kmpshowcase.domain.repository.DateRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class GetTodayDateUseCase(
    private val dateRepository: DateRepository,
) : UseCase<None, LocalDate>() {
    override suspend fun run(params: None): LocalDate = dateRepository.today()
}
