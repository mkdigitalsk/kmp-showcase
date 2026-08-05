package sk.mkdigital.kmpshowcase.presentation.screen.home

import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel
import sk.mkdigital.kmpshowcase.presentation.base.NavEvent

class HomeViewModel : BaseViewModel<HomeUiState>(HomeUiState()) {

    fun onFeatureClick(featureId: FeatureId) {
        navigate(HomeNavEvent.ToFeature(featureId))
    }
}

data class HomeUiState(
    val features: List<Feature> = showcaseFeatures
)

sealed interface HomeNavEvent : NavEvent {
    data class ToFeature(val featureId: FeatureId) : HomeNavEvent
}
