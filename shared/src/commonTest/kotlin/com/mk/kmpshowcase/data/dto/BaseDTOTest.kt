package com.mk.kmpshowcase.data.dto

import kotlinx.serialization.json.Json

abstract class BaseDTOTest {

    protected val json = Json { ignoreUnknownKeys = true }

    protected inline fun <reified T> runTest(
        serverJson: String,
        crossinline assertions: (T) -> Unit
    ) {
        val parsed = json.decodeFromString<T>(serverJson)
        assertions(parsed)
    }

    protected inline fun <reified T> runTest(
        serverJson: String,
        deserializer: (String) -> T,
        crossinline assertions: (T) -> Unit
    ) {
        val parsed = deserializer(serverJson)
        assertions(parsed)
    }
}
