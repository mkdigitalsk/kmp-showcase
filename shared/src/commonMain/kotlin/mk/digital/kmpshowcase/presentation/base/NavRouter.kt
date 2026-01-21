package mk.digital.kmpshowcase.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import mk.digital.kmpshowcase.presentation.base.router.ExternalRouter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue
import kotlin.reflect.KClass

/**
 * Navigation Router interface for Navigation 3.
 * Works with NavBackStack instead of NavController.
 */
interface NavRouter<T: NavKey> {
    val backStack: NavBackStack<T>
    fun navigateTo(page: T)
    fun <R : Any> navigateTo(page: T, popUpTo: KClass<R>? = null, inclusive: Boolean = false)
    fun onBack()
    fun replaceAll(page: T)
    fun openLink(url: String)
    fun dial(number: String)
    fun share(text: String)
}

/**
 * Implementation of NavRouter for Navigation 3.
 * Wraps NavBackStack which is created in composable scope.
 */
class NavRouterImpl<T : NavKey>(
    override val backStack: NavBackStack<T>,
) : NavRouter<T>, KoinComponent {

    private val externalRouter: ExternalRouter by inject()

    override fun navigateTo(page: T) {
        backStack.add(page)
    }

    override fun <R : Any> navigateTo(page: T, popUpTo: KClass<R>?, inclusive: Boolean) {
        if (popUpTo != null) {
            // Pop items until we reach one of type popUpTo
            while (backStack.lastOrNull()?.let { !popUpTo.isInstance(it) } == true) {
                backStack.removeLastOrNull()
            }
            // Now last item is either of type popUpTo, or backstack is empty
            if (inclusive) {
                backStack.removeLastOrNull()
            }
        }
        backStack.add(page)
    }

    override fun onBack() {
        backStack.removeLastOrNull()
    }

    override fun replaceAll(page: T) {
        // Clear backstack and add new page
        while (backStack.removeLastOrNull() != null) {
            // Keep removing until empty
        }
        backStack.add(page)
    }

    override fun openLink(url: String) = externalRouter.openLink(url)

    override fun dial(number: String) = externalRouter.dial(number)

    override fun share(text: String) = externalRouter.share(text)
}

/**
 * Creates and remembers a NavRouter with internal backStack management.
 */
@Suppress("UNCHECKED_CAST")
@Composable
fun <T : NavKey> rememberNavRouter(
    config: SavedStateConfiguration,
    initialRoute: T
): NavRouter<T> {
    val backStack = rememberNavBackStack(config, initialRoute)
    return remember(backStack) {
        NavRouterImpl(backStack as NavBackStack<T>)
    }
}

/**
 * Returns the standard entry decorators for Navigation 3.
 * Includes SaveableStateHolder for state persistence and ViewModelStore for ViewModel scoping.
 */
@Composable
fun <T : NavKey> rememberNavEntryDecorators(): List<NavEntryDecorator<T>> = listOf(
    rememberSaveableStateHolderNavEntryDecorator(),
    rememberViewModelStoreNavEntryDecorator()
)
