package sk.mkdigital.kmpshowcase.data.repository

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.test
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRepositoryImplTest : BaseTest<SettingsRepositoryImpl>() {
    override lateinit var classUnderTest: SettingsRepositoryImpl

    private val appPreferences: PersistentPreferences = mock()

    override fun beforeEach() {
        classUnderTest = SettingsRepositoryImpl(appPreferences)
    }

    @Test
    fun `getThemeMode returns LIGHT when preferences returns LIGHT`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.getThemeMode() } returns "LIGHT"
            },
            whenAction = {
                classUnderTest.getThemeMode()
            },
            then = { result ->
                assertEquals(ThemeMode.LIGHT, result)
                verifySuspend { appPreferences.getThemeMode() }
            }
        )
    }

    @Test
    fun `getThemeMode returns DARK when preferences returns DARK`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.getThemeMode() } returns "DARK"
            },
            whenAction = {
                classUnderTest.getThemeMode()
            },
            then = { result ->
                assertEquals(ThemeMode.DARK, result)
            }
        )
    }

    @Test
    fun `getThemeMode returns SYSTEM when preferences returns SYSTEM`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.getThemeMode() } returns "SYSTEM"
            },
            whenAction = {
                classUnderTest.getThemeMode()
            },
            then = { result ->
                assertEquals(ThemeMode.SYSTEM, result)
            }
        )
    }

    @Test
    fun `getThemeMode returns SYSTEM as default for unknown value`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.getThemeMode() } returns "UNKNOWN"
            },
            whenAction = {
                classUnderTest.getThemeMode()
            },
            then = { result ->
                assertEquals(ThemeMode.SYSTEM, result)
            }
        )
    }

    @Test
    fun `setThemeMode saves LIGHT mode to preferences`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.setThemeMode("LIGHT") } returns Unit
            },
            whenAction = {
                classUnderTest.setThemeMode(ThemeMode.LIGHT)
            },
            then = {
                verifySuspend { appPreferences.setThemeMode("LIGHT") }
            }
        )
    }

    @Test
    fun `setThemeMode saves DARK mode to preferences`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.setThemeMode("DARK") } returns Unit
            },
            whenAction = {
                classUnderTest.setThemeMode(ThemeMode.DARK)
            },
            then = {
                verifySuspend { appPreferences.setThemeMode("DARK") }
            }
        )
    }

    @Test
    fun `setThemeMode saves SYSTEM mode to preferences`() = runTest {
        test(
            given = {
                everySuspend { appPreferences.setThemeMode("SYSTEM") } returns Unit
            },
            whenAction = {
                classUnderTest.setThemeMode(ThemeMode.SYSTEM)
            },
            then = {
                verifySuspend { appPreferences.setThemeMode("SYSTEM") }
            }
        )
    }
}
