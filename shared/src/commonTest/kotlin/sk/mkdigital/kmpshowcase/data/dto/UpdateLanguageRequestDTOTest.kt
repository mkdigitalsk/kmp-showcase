package sk.mkdigital.kmpshowcase.data.dto

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.contracts.LanguageDTO
import sk.mkdigital.kmpshowcase.contracts.user.UpdateLanguageRequestDTO
import sk.mkdigital.kmpshowcase.data.network.applyCommonConfig
import sk.mkdigital.kmpshowcase.fake.NoOpLogger
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateLanguageRequestDTOTest {

    @Test
    fun `the language pick goes out as its key under language`() = runTest {
        var sent = ""
        val client = HttpClient(MockEngine) {
            applyCommonConfig("example.invalid", BuildType.DEBUG, NoOpLogger)
            engine {
                addHandler { request ->
                    sent = request.body.toByteArray().decodeToString()
                    respond(content = "", status = HttpStatusCode.NoContent)
                }
            }
        }

        client.put("users/me/locale") { setBody(UpdateLanguageRequestDTO(LanguageDTO.SK)) }

        assertEquals(buildJsonObject { put("language", JsonPrimitive("SK")) }, Json.parseToJsonElement(sent))
    }
}
