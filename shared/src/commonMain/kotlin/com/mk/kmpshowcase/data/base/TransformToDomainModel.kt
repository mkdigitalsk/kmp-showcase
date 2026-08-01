package com.mk.kmpshowcase.data.base

interface TransformToDomainModel<out DomainModel> {
    fun transform(): DomainModel
}
