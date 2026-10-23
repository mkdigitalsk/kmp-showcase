package sk.mkdigital.kmpshowcase.presentation.screen.signUp

import app.cash.turbine.test
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignUpUseCase
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModelTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SignUpViewModelTest : BaseViewModelTest() {

    private val signUpUseCase = mock<SignUpUseCase>()

    private fun createViewModel(): SignUpViewModel = SignUpViewModel(signUpUseCase = signUpUseCase)

    @Test
    fun `openTerms opens the terms of use the sign up concludes a contract on`() = runTest {
        val viewModel = createViewModel()

        viewModel.navEvent.test {
            viewModel.openTerms()

            assertEquals(SignUpNavEvent.OpenTerms("https://showcase.mkdigital.sk/terms"), awaitItem())
        }
    }

    @Test
    fun `openPrivacy opens the privacy notice`() = runTest {
        val viewModel = createViewModel()

        viewModel.navEvent.test {
            viewModel.openPrivacy()

            assertEquals(SignUpNavEvent.OpenPrivacy("https://showcase.mkdigital.sk/privacy"), awaitItem())
        }
    }
}
