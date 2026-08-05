package sk.mkdigital.kmpshowcase.domain.repository

fun interface ClearableCache {
    suspend fun clear()
}
