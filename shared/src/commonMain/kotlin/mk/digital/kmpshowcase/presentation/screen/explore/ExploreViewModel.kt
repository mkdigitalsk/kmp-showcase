package mk.digital.kmpshowcase.presentation.screen.explore

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import mk.digital.kmpshowcase.presentation.base.BaseViewModel

class ExploreViewModel : BaseViewModel<Unit>(Unit) {
    @Composable
    override fun toolbarTitle(): String = "Explore"
    override val navIcon: ImageVector? = null
}
