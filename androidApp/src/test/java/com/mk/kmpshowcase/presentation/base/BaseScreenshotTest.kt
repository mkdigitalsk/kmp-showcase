package com.mk.kmpshowcase.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.mk.kmpshowcase.presentation.base.rule.LocaleRule
import com.mk.kmpshowcase.presentation.base.rule.TimezoneRule
import com.mk.kmpshowcase.presentation.foundation.AppTheme
import com.mk.kmpshowcase.presentation.foundation.ThemeMode
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.context.stopKoin
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Internal wrapper for short test names */
class StateHolder<T>(val state: T, private val name: String) {
    override fun toString(): String = name
}

enum class ScreenshotMode {
    LIGHT, NIGHT
}

@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5, sdk = [35])
abstract class BaseScreenshotTest<T>(
    private val stateHolder: StateHolder<T>,
    private val mode: ScreenshotMode,
) {
    /** Access the actual state */
    protected val state: T get() = stateHolder.state

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val localeRule: LocaleRule = LocaleRule()

    @get:Rule
    val timezoneRule: TimezoneRule = TimezoneRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    fun screenshot(content: @Composable () -> Unit) {
        val themeMode = when (mode) {
            ScreenshotMode.LIGHT -> ThemeMode.LIGHT
            ScreenshotMode.NIGHT -> ThemeMode.DARK
        }

        composeRule.setContent {
            AppTheme(themeMode = themeMode) {
                content()
            }
        }

        composeRule.onRoot().captureRoboImage(
            filePath = "src/test/snapshots/images/${getFileName()}.png"
        )
    }

    private fun getFileName(): String {
        val modeSuffix = when (mode) {
            ScreenshotMode.LIGHT -> "light"
            ScreenshotMode.NIGHT -> "night"
        }
        return "${this::class.java.name}_${stateHolder}_$modeSuffix"
    }
}

inline fun <reified T : Any> generateParameterizedData(
    viewStatesProvider: PreviewParameterProvider<T>,
): Collection<*> {
    val viewStates = viewStatesProvider.values.toList()
    val modes = listOf(ScreenshotMode.LIGHT, ScreenshotMode.NIGHT)
    val typeName = T::class.simpleName ?: "State"
    return viewStates.flatMapIndexed { index, state ->
        modes.map { mode ->
            arrayOf(StateHolder(state, "${typeName}_$index"), mode)
        }
    }
}
