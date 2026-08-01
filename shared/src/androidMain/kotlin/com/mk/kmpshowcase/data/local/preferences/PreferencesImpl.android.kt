package com.mk.kmpshowcase.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first

actual class PreferencesImpl(
    context: Context,
    actual override val storageName: String
) : com.mk.kmpshowcase.data.local.preferences.Preferences {

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(storageName) }
    )

    actual override suspend fun putString(key: String, value: String?) =
        setOrRemoveIfNull(stringPreferencesKey(key), value)

    actual override suspend fun getString(key: String): String? =
        getValue(stringPreferencesKey(key))

    actual override suspend fun putInt(key: String, value: Int?) =
        setOrRemoveIfNull(intPreferencesKey(key), value)

    actual override suspend fun getInt(key: String): Int? =
        getValue(intPreferencesKey(key))

    actual override suspend fun putBoolean(key: String, value: Boolean?) =
        setOrRemoveIfNull(booleanPreferencesKey(key), value)

    actual override suspend fun getBoolean(key: String): Boolean? =
        getValue(booleanPreferencesKey(key))

    actual override suspend fun putFloat(key: String, value: Float?) =
        setOrRemoveIfNull(floatPreferencesKey(key), value)

    actual override suspend fun getFloat(key: String): Float? =
        getValue(floatPreferencesKey(key))

    actual override suspend fun putLong(key: String, value: Long?) =
        setOrRemoveIfNull(longPreferencesKey(key), value)

    actual override suspend fun getLong(key: String): Long? =
        getValue(longPreferencesKey(key))

    actual override suspend fun putDouble(key: String, value: Double?) =
        setOrRemoveIfNull(doublePreferencesKey(key), value)

    actual override suspend fun getDouble(key: String): Double? =
        getValue(doublePreferencesKey(key))

    actual override suspend fun remove(key: String) {
        dataStore.edit {
            it.remove(stringPreferencesKey(key))
            it.remove(intPreferencesKey(key))
            it.remove(booleanPreferencesKey(key))
            it.remove(floatPreferencesKey(key))
            it.remove(longPreferencesKey(key))
            it.remove(doublePreferencesKey(key))
        }
    }

    actual override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private suspend fun <T> setOrRemoveIfNull(key: Preferences.Key<T>, value: T?) {
        dataStore.edit {
            if (value == null) {
                it.remove(key)
            } else
                it[key] = value
        }
    }

    private suspend fun <T> getValue(key: Preferences.Key<T>): T? {
        return dataStore.data.first()[key]
    }
}
