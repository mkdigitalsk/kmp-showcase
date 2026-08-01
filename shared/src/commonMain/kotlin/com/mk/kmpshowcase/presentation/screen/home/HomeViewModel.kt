package com.mk.kmpshowcase.presentation.screen.home

import com.mk.kmpshowcase.presentation.base.BaseViewModel
import com.mk.kmpshowcase.presentation.base.NavEvent

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
