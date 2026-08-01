package com.mk.kmpshowcase.data.local.preferences

import platform.Foundation.NSUserDefaults

actual class PreferencesImpl(actual override val storageName: String) : Preferences {

    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults
    private fun storageKey(key: String) = "${storageName}_$key"

    actual override suspend fun putString(key: String, value: String?) =
        setOrRemoveIfNull(key, value, defaults::setObject)

    actual override suspend fun getString(key: String): String? {
        return defaults.stringForKey(storageKey(key))
    }

    actual override suspend fun putBoolean(key: String, value: Boolean?) =
        setOrRemoveIfNull(key, value, defaults::setBool)

    actual override suspend fun getBoolean(key: String): Boolean? =
        getOrNull(key) { defaults.boolForKey(it) }

    actual override suspend fun putInt(key: String, value: Int?) =
        setOrRemoveIfNull(key, value?.toLong(), defaults::setInteger)

    actual override suspend fun getInt(key: String): Int? =
        getOrNull(key) { defaults.integerForKey(it).toInt() }

    actual override suspend fun putLong(key: String, value: Long?) =
        setOrRemoveIfNull(key, value, defaults::setInteger)

    actual override suspend fun getLong(key: String): Long? =
        getOrNull(key) { defaults.integerForKey(it) }

    actual override suspend fun putFloat(key: String, value: Float?) =
        setOrRemoveIfNull(key, value, defaults::setFloat)

    actual override suspend fun getFloat(key: String): Float? =
        getOrNull(key) { defaults.floatForKey(it) }

    actual override suspend fun putDouble(key: String, value: Double?) =
        setOrRemoveIfNull(key, value, defaults::setDouble)

    actual override suspend fun getDouble(key: String): Double? =
        getOrNull(key) { defaults.doubleForKey(it) }

    actual override suspend fun remove(key: String) {
        defaults.removeObjectForKey(storageKey(key))
    }

    actual override suspend fun clear() {
        val prefix = "${storageName}_"
        defaults.dictionaryRepresentation().keys
            .mapNotNull { it as? String }
            .filter { it.startsWith(prefix) }
            .forEach { defaults.removeObjectForKey(it) }
    }

    private inline fun <T> getOrNull(key: String, getter: (String) -> T): T? {
        val storageKey = storageKey(key)
        return if (defaults.objectForKey(storageKey) != null) getter(storageKey) else null
    }

    private inline fun <T> setOrRemoveIfNull(
        key: String,
        value: T?,
        setter: (value: T, key: String) -> Unit
    ) {
        val storageKey = storageKey(key)
        if (value == null) {
            defaults.removeObjectForKey(storageKey)
        } else {
            setter(value, storageKey)
        }
    }
}
