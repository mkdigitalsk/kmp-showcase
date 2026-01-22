package mk.digital.kmpshowcase.domain.repository

fun interface ClearableCache {
    suspend fun clear()
}
