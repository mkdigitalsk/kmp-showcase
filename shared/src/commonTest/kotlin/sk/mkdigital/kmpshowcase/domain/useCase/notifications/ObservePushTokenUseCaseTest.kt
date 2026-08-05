package sk.mkdigital.kmpshowcase.domain.useCase.notifications

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.repository.PushNotificationService
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class ObservePushTokenUseCaseTest : BaseTest<ObservePushTokenUseCase>() {
    override lateinit var classUnderTest: ObservePushTokenUseCase

    private val pushNotificationService: PushNotificationService = mock()

    override fun beforeEach() {
        classUnderTest = ObservePushTokenUseCase(pushNotificationService)
    }

    @Test
    fun `invoke returns flow with initial token`() = runTest {
        val tokenFlow = MutableStateFlow<String?>("test-token")

        every { pushNotificationService.token } returns tokenFlow

        val result = classUnderTest().first()

        assertEquals("test-token", result)
        verify { pushNotificationService.token }
    }

    @Test
    fun `invoke returns flow with null token`() = runTest {
        val tokenFlow = MutableStateFlow<String?>(null)

        every { pushNotificationService.token } returns tokenFlow

        val result = classUnderTest().first()

        assertEquals(null, result)
    }
}
