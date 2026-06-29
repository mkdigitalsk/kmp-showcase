package com.mk.kmpshowcase.server.core.mail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// Sends mail via the Resend HTTP API — works where outbound SMTP is blocked (e.g. Railway).
// Selected when RESEND_API_KEY is set; the from-domain must be verified in Resend.
internal class ResendMailer(private val config: MailConfig) : Mailer {

    private val client = HttpClient.newHttpClient()

    @Serializable
    private data class Email(
        val from: String,
        val to: List<String>,
        val subject: String,
        val text: String,
        @SerialName("reply_to") val replyTo: String? = null,
    )

    override suspend fun send(to: String, subject: String, body: String, replyTo: String?) {
        val payload = Json.encodeToString(Email(config.from, listOf(to), subject, body, replyTo))
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.resend.com/emails"))
            .header("Authorization", "Bearer ${config.resendApiKey}")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build()
        withContext(Dispatchers.IO) {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            check(response.statusCode() in 200..299) {
                "Resend API ${response.statusCode()}: ${response.body()}"
            }
        }
    }
}
