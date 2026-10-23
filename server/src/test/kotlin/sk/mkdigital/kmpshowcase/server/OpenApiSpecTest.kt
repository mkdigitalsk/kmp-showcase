package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.plugins.configureAuth
import sk.mkdigital.kmpshowcase.server.plugins.configureRateLimit
import sk.mkdigital.kmpshowcase.server.plugins.configureRouting
import sk.mkdigital.kmpshowcase.server.plugins.configureSerialization
import sk.mkdigital.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Writes the specification the compiler plugin infers from the routing tree to contracts/openapi.json — committed, so a
 * changed path or payload lands as a diff there rather than as an undefined field in a consumer months later.
 */
class OpenApiSpecTest {

    @Test
    fun `the specification describes the routes and the credential they ask for`() = testApplication {
        environment { config = MapApplicationConfig() }
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth(jwtConfig)
            configureRateLimit()
            configureRouting(AppDependencies(jwtConfig))
        }

        val spec = client.get("/openapi.json").bodyAsText()

        File("../contracts/openapi.json").writeText(spec)
        assertTrue("${ApiVersion.BASE}/notes" in pathsOf(spec), "the routing tree produced no paths")
        assertTrue(schemasOf(spec).isNotEmpty(), "inference described no payload — is the compiler plugin applied?")
        assertEverySchemaResolved(spec)
        assertEverySecurityNameDeclared(spec)
        assertEachTypePublishedOnce(spec)
    }

    private fun pathsOf(spec: String): Set<String> =
        Json.parseToJsonElement(spec).jsonObject.getValue("paths").jsonObject.keys

    private fun schemasOf(spec: String): Map<String, JsonElement> =
        Json.parseToJsonElement(spec).jsonObject["components"]?.jsonObject?.get("schemas")?.jsonObject.orEmpty()

    private fun securitySchemesOf(spec: String): Map<String, JsonElement> =
        Json.parseToJsonElement(spec).jsonObject["components"]?.jsonObject?.get("securitySchemes")?.jsonObject.orEmpty()

    /** Each scheme name a `security` requirement carries — the document's own and every operation's. */
    private fun securityNamesOf(spec: String): Set<String> {
        val document = Json.parseToJsonElement(spec).jsonObject
        val operations = document.getValue("paths").jsonObject.values
            .flatMap { pathItem -> pathItem.jsonObject.values }
            .filterIsInstance<JsonObject>()
        return (operations + document)
            .flatMap { it["security"]?.jsonArray.orEmpty() }
            .flatMap { requirement -> requirement.jsonObject.keys }
            .toSet()
    }

    /**
     * A route typed Any publishes no schema, and the gap only shows up as an untyped payload in a consumer. The message
     * names the type that did not resolve.
     */
    private fun assertEverySchemaResolved(spec: String) {
        assertFalse(
            spec.contains("Failed to resolve schema"),
            "a route publishes no response schema — give the handler or its helper a concrete return type:\n" +
                spec.lineSequence().filter { it.contains("Failed to resolve schema") }.joinToString("\n"),
        )
    }

    /**
     * A requirement naming no declared scheme leaves a consumer's generator no credential to send.
     *
     * [OpenAPI 3.1.1 — Security Requirement Object](https://spec.openapis.org/oas/v3.1.1.html#security-requirements-name)
     * — "Each name MUST correspond to a security scheme which is declared in the Security Schemes under the Components
     * Object."
     */
    private fun assertEverySecurityNameDeclared(spec: String) {
        val required = securityNamesOf(spec)

        assertTrue(required.isNotEmpty(), "no operation requires a credential")
        assertEquals(emptySet(), required - securitySchemesOf(spec).keys, "a requirement names an undeclared scheme")
    }

    /**
     * The generator gives a type referenced both nullable and non-null a second, numbered component — `NoteResponseDTO2`
     * — and a consumer then types one payload under two names.
     *
     * [KTOR-9895](https://youtrack.jetbrains.com/issue/KTOR-9895) — "Nullability belongs to the referencing property, but
     * it is written into the shared component"
     */
    private fun assertEachTypePublishedOnce(spec: String) {
        val names = schemasOf(spec).keys
        val twins = names.filter { name -> names.any { type -> name.isNumberedAfter(type) } }
        assertTrue(twins.isEmpty(), "a type is published a second time, under a numbered name: $twins")
    }

    /** Whether this name is [type]'s with a number after it — the name the generator gives a second component of one title. */
    private fun String.isNumberedAfter(type: String): Boolean =
        length > type.length && startsWith(type) && drop(type.length).all(Char::isDigit)
}

private val jwtConfig = JwtConfig(
    secret = "test-secret-at-least-32-bytes-long-for-hs256",
    issuer = "kmp-showcase",
    audience = "kmp-showcase-users",
)
