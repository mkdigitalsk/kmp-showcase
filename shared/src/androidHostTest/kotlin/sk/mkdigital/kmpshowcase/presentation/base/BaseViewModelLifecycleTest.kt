package sk.mkdigital.kmpshowcase.presentation.base

import kotlin.test.Test
import kotlin.test.assertEquals

class BaseViewModelLifecycleTest {

    private class Screen : BaseViewModel<Unit>(Unit) {
        var creates = 0
            private set

        override fun onCreate() {
            creates++
        }
    }

    @Test
    fun `the screen is created once, however often it re-enters composition`() {
        val screen = Screen()

        screen.onCreated()
        screen.onCreated()
        screen.onCreated()

        assertEquals(1, screen.creates)
    }

    @Test
    fun `a second screen creates on its own`() {
        val first = Screen().apply { onCreated() }
        val second = Screen()

        second.onCreated()

        assertEquals(1, first.creates)
        assertEquals(1, second.creates)
    }
}
