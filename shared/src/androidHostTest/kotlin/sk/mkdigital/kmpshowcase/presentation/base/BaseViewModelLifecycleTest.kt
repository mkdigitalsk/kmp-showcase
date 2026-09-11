package sk.mkdigital.kmpshowcase.presentation.base

import kotlin.test.Test
import kotlin.test.assertEquals

class BaseViewModelLifecycleTest {

    private class Screen : BaseViewModel<Unit>(Unit) {
        var creates = 0
            private set

        override fun onCreated() {
            creates++
        }
    }

    @Test
    fun `the screen is created once, however often it re-enters composition`() {
        val screen = Screen()

        screen.onEnteredComposition()
        screen.onEnteredComposition()
        screen.onEnteredComposition()

        assertEquals(1, screen.creates)
    }

    @Test
    fun `a second screen creates on its own`() {
        val first = Screen().apply { onEnteredComposition() }
        val second = Screen()

        second.onEnteredComposition()

        assertEquals(1, first.creates)
        assertEquals(1, second.creates)
    }
}
