package sk.mkdigital.kmpshowcase.data.client

import sk.mkdigital.kmpshowcase.contracts.auth.AuthResponseDTO
import sk.mkdigital.kmpshowcase.contracts.auth.SignInRequestDTO
import sk.mkdigital.kmpshowcase.contracts.auth.SignUpRequestDTO
import sk.mkdigital.kmpshowcase.data.network.handleApiCall
import sk.mkdigital.kmpshowcase.domain.exceptions.base.ApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

interface AuthClient {
    suspend fun signIn(email: String, password: String): AuthResponseDTO
    suspend fun signUp(email: String, password: String): AuthResponseDTO
    suspend fun me(token: String): AuthResponseDTO
    suspend fun deleteAccount()
}

class AuthClientImpl(
    private val client: HttpClient
) : AuthClient {

    override suspend fun signIn(email: String, password: String): AuthResponseDTO = handleApiCall {
        client.post("auth/sign-in") {
            setBody(SignInRequestDTO(email, password))
        }.body()
    }

    override suspend fun signUp(email: String, password: String): AuthResponseDTO = handleApiCall {
        client.post("auth/sign-up") {
            setBody(SignUpRequestDTO(email, password))
        }.body()
    }

    override suspend fun me(token: String): AuthResponseDTO = handleApiCall {
        client.post("auth/token") {
            bearerAuth(token)
        }.body()
    }

    // The client is built without expectSuccess, so a rejected delete arrives as a response rather than
    // as a throw, and an unchecked status would report a live account as deleted.
    override suspend fun deleteAccount() = handleApiCall {
        val response = client.delete("users/me")
        if (!response.status.isSuccess()) {
            throw ApiException(
                httpCode = response.status.value,
                message = "Delete account failed: ${response.status.description}",
            )
        }
    }
}
