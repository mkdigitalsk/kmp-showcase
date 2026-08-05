package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.test
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test

class LogPushTokenUseCaseTest : BaseTest<LogPushTokenUseCase>() {
    override lateinit var classUnderTest: LogPushTokenUseCase

    private val pushNotificationService: PushNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = LogPushTokenUseCase(pushNotificationService)
    }

    @Test
    fun `invoke calls service logToken`() = runTest {
        test(
            given = {
                everySuspend { pushNotificationService.logToken() } returns Unit
            },
            whenAction = {
                classUnderTest()
            },
            then = {
                verifySuspend { pushNotificationService.logToken() }
            }
        )
    }
}
