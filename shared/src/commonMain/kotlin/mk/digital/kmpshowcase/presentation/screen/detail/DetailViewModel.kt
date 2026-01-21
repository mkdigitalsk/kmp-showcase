package mk.digital.kmpshowcase.presentation.screen.detail

import androidx.compose.runtime.Composable
import mk.digital.kmpshowcase.presentation.base.BaseViewModel

class DetailViewModel(
    val id: Int,
) : BaseViewModel<Unit>(Unit) {
    @Composable
    override fun toolbarTitle(): String = id.toString()
}
