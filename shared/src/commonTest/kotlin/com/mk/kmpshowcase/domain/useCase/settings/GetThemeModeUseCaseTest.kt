package com.mk.kmpshowcase.domain.useCase.settings

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.SettingsRepository
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.domain.useCase.base.invoke
import com.mk.kmpshowcase.presentation.foundation.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetThemeModeUseCaseTest : BaseTest<GetThemeModeUseCase>() {
    override lateinit var classUnderTest: GetThemeModeUseCase

    private val settingsRepository: SettingsRepository = mock()

    override fun beforeEach() {
        classUnderTest = GetThemeModeUseCase(settingsRepository)
    }

    @Test
    fun `invoke returns LIGHT mode from repository`() = runTest {
        test(
            given = {
                everySuspend { settingsRepository.getThemeMode() } returns ThemeMode.LIGHT
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(ThemeMode.LIGHT, result)
            }
        )
    }

    @Test
    fun `invoke returns DARK mode from repository`() = runTest {
        test(
            given = {
                everySuspend { settingsRepository.getThemeMode() } returns ThemeMode.DARK
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(ThemeMode.DARK, result)
            }
        )
    }

    @Test
    fun `invoke returns SYSTEM mode from repository`() = runTest {
        test(
            given = {
                everySuspend { settingsRepository.getThemeMode() } returns ThemeMode.SYSTEM
            },
            whenAction = {
                classUnderTest()
            },
            then = { result ->
                assertEquals(ThemeMode.SYSTEM, result)
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Settings error")

        everySuspend { settingsRepository.getThemeMode() } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest()
        }
    }
}
