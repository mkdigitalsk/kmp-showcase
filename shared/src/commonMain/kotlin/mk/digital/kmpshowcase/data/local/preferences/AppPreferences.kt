package mk.digital.kmpshowcase.data.local.preferences

interface AppPreferences {
    suspend fun getPersistentCounter(): Int
    suspend fun setPersistentCounter(value: Int)
}

class AppPreferencesImpl(private val preferences: Preferences) : AppPreferences {

    override suspend fun getPersistentCounter(): Int = preferences.getInt(PERSISTENT_COUNTER_KEY) ?: 0
    override suspend fun setPersistentCounter(value: Int) = preferences.putInt(PERSISTENT_COUNTER_KEY, value)

    private companion object {
        private const val PERSISTENT_COUNTER_KEY = "persistent_counter"
    }
}
