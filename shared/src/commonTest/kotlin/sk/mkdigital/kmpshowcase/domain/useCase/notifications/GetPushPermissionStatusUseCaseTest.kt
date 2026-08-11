package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.repository.PushPermissionStatus
import sk.mkdigital.kmpshowcase.domain.test
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPushPermissionStatusUseCaseTest : BaseTest<GetPushPermissionStatusUseCase>() {
    override lateinit var classUnderTest: GetPushPermissionStatusUseCase

    private val pushNotificationService: PushNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = GetPushPermissionStatusUseCase(pushNotificationService)
    }

    @Test
    fun `invoke returns GRANTED when permission is granted`() = runTest {
        test(
            given = {
                everySuspend { pushNotificationService.getPermissionStatus() } returns PushPermissionStatus.GRANTED
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(PushPermissionStatus.GRANTED, result)
                verifySuspend { pushNotificationService.getPermissionStatus() }
            }
        )
    }

    @Test
    fun `invoke returns DENIED when permission is denied`() = runTest {
        test(
            given = {
                everySuspend { pushNotificationService.getPermissionStatus() } returns PushPermissionStatus.DENIED
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(PushPermissionStatus.DENIED, result)
            }
        )
    }

    @Test
    fun `invoke returns NOT_DETERMINED when permission is not determined`() = runTest {
        test(
            given = {
                everySuspend { pushNotificationService.getPermissionStatus() } returns
                    PushPermissionStatus.NOT_DETERMINED
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(PushPermissionStatus.NOT_DETERMINED, result)
            }
        )
    }
}
