package com.mk.kmpshowcase.data.local.preferences

interface Preferences {
    val storageName: String

    suspend fun putString(key: String, value: String?)
    suspend fun getString(key: String): String?

    suspend fun putBoolean(key: String, value: Boolean?)
    suspend fun getBoolean(key: String): Boolean?

    suspend fun putInt(key: String, value: Int?)
    suspend fun getInt(key: String): Int?

    suspend fun putFloat(key: String, value: Float?)
    suspend fun getFloat(key: String): Float?

    suspend fun putLong(key: String, value: Long?)
    suspend fun getLong(key: String): Long?

    suspend fun putDouble(key: String, value: Double?)
    suspend fun getDouble(key: String): Double?

    suspend fun remove(key: String)
    suspend fun clear()
}

expect class PreferencesImpl : Preferences {
    override val storageName: String

    override suspend fun putString(key: String, value: String?)
    override suspend fun getString(key: String): String?

    override suspend fun putBoolean(key: String, value: Boolean?)
    override suspend fun getBoolean(key: String): Boolean?

    override suspend fun putInt(key: String, value: Int?)
    override suspend fun getInt(key: String): Int?

    override suspend fun putFloat(key: String, value: Float?)
    override suspend fun getFloat(key: String): Float?

    override suspend fun putLong(key: String, value: Long?)
    override suspend fun getLong(key: String): Long?

    override suspend fun putDouble(key: String, value: Double?)
    override suspend fun getDouble(key: String): Double?

    override suspend fun remove(key: String)
    override suspend fun clear()
}
