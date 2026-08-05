package sk.mkdigital.kmpshowcase.domain.useCase

import sk.mkdigital.kmpshowcase.domain.model.User
import sk.mkdigital.kmpshowcase.domain.repository.UserRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class GetUsersUseCase(
    private val userRepository: UserRepository
) : UseCase<None, List<User>>() {
    override suspend fun run(params: None): List<User> = userRepository.getUsers()
}
