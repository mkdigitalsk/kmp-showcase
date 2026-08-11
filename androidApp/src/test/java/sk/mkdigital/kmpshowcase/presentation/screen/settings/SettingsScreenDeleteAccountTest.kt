package sk.mkdigital.kmpshowcase.presentation.screen.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import sk.mkdigital.kmpshowcase.presentation.base.rule.LocaleRule
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5, sdk = [35])
class SettingsScreenDeleteAccountTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val localeRule: LocaleRule = LocaleRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `a demo account is told why instead of being offered the control`() {
        setSettingsContent(demoAccount = true)

        scrollTo(DEMO_NOTICE)

        composeRule.onNodeWithText(DEMO_NOTICE).assertIsDisplayed()
        composeRule.onNodeWithText(DELETE_ACCOUNT).assertDoesNotExist()
    }

    @Test
    fun `a normal account is offered the control`() {
        setSettingsContent(demoAccount = false)

        scrollTo(DELETE_ACCOUNT)

        composeRule.onNodeWithText(DELETE_ACCOUNT).assertIsDisplayed()
        composeRule.onNodeWithText(DEMO_NOTICE).assertDoesNotExist()
    }

    private fun setSettingsContent(demoAccount: Boolean) {
        composeRule.setContent {
            AppTheme {
                SettingsScreen(
                    state = SettingsState(
                        isDemoAccount = demoAccount,
                        showCrashButton = false,
                        versionName = "1.0.0",
                        versionCode = "1"
                    )
                )
            }
        }
    }

    // The row sits last in a LazyColumn, so nothing below the viewport is composed until scrolled to —
    // an unscrolled assertDoesNotExist would pass on a screen that renders the control perfectly well.
    private fun scrollTo(text: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    private companion object {
        const val DELETE_ACCOUNT = "Delete account"
        const val DEMO_NOTICE = "This is a demo account, so it cannot be deleted."
    }
}
