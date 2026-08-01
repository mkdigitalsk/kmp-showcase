package com.mk.kmpshowcase.domain.useCase.settings

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.SettingsRepository
import com.mk.kmpshowcase.domain.test
import com.mk.kmpshowcase.presentation.foundation.ThemeMode
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SetThemeModeUseCaseTest : BaseTest<SetThemeModeUseCase>() {
    override lateinit var classUnderTest: SetThemeModeUseCase

    private val settingsRepository: SettingsRepository = mock()

    override fun beforeEach() {
        classUnderTest = SetThemeModeUseCase(settingsRepository)
    }

    @Test
    fun `invoke sets LIGHT mode in repository`() = runTest {
        test(
            given = {
                everySuspend { settingsRepository.setThemeMode(ThemeMode.LIGHT) } returns Unit
            },
            whenAction = {
                classUnderTest(ThemeMode.LIGHT)
            },
            then = {
                verifySuspend { settingsRepository.setThemeMode(ThemeMode.LIGHT) }
            }
        )
    }

    @Test
    fun `invoke sets DARK mode in repository`() = runTest {
        test(
            given = {
                everySuspend { settingsRepository.setThemeMode(ThemeMode.DARK) } returns Unit
            },
            whenAction = {
                classUnderTest(ThemeMode.DARK)
            },
            then = {
                verifySuspend { settingsRepository.setThemeMode(ThemeMode.DARK) }
            }
        )
    }

    @Test
    fun `invoke sets SYSTEM mode in repository`() = runTest {
        test(
            given = {
                everySuspend { settingsRepository.setThemeMode(ThemeMode.SYSTEM) } returns Unit
            },
            whenAction = {
                classUnderTest(ThemeMode.SYSTEM)
            },
            then = {
                verifySuspend { settingsRepository.setThemeMode(ThemeMode.SYSTEM) }
            }
        )
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val exception = RuntimeException("Settings error")

        everySuspend { settingsRepository.setThemeMode(ThemeMode.DARK) } throws exception

        assertFailsWith<RuntimeException> {
            classUnderTest(ThemeMode.DARK)
        }
    }
}
