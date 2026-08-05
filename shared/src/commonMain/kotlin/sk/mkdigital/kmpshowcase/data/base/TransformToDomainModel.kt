package sk.mkdigital.kmpshowcase.data.base

interface TransformToDomainModel<out DomainModel> {
    fun transform(): DomainModel
}
