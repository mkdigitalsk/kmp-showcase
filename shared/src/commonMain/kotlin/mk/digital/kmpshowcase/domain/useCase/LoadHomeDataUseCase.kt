package mk.digital.kmpshowcase.domain.useCase

import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.domain.repository.UserRepository
import mk.digital.kmpshowcase.domain.useCase.base.None
import mk.digital.kmpshowcase.domain.useCase.base.UseCase

class LoadHomeDataUseCase(
    private val userRepository: UserRepository
) : UseCase<None, List<User>>() {
    override suspend fun run(params: None): List<User> = userRepository.getUsers()
}
