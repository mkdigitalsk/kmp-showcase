package sk.mkdigital.kmpshowcase.data.client

import sk.mkdigital.kmpshowcase.contracts.auth.AuthResponseDTO
import sk.mkdigital.kmpshowcase.contracts.auth.SignInRequestDTO
import sk.mkdigital.kmpshowcase.contracts.auth.SignUpRequestDTO
import sk.mkdigital.kmpshowcase.data.network.handleApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface AuthClient {
    suspend fun signIn(email: String, password: String): AuthResponseDTO
    suspend fun signUp(email: String, password: String, name: String): AuthResponseDTO
    suspend fun me(token: String): AuthResponseDTO
}

class AuthClientImpl(
    private val client: HttpClient
) : AuthClient {

    override suspend fun signIn(email: String, password: String): AuthResponseDTO = handleApiCall {
        client.post("auth/sign-in") {
            setBody(SignInRequestDTO(email, password))
        }.body()
    }

    override suspend fun signUp(email: String, password: String, name: String): AuthResponseDTO = handleApiCall {
        client.post("auth/sign-up") {
            setBody(SignUpRequestDTO(email, password, name))
        }.body()
    }

    override suspend fun me(token: String): AuthResponseDTO = handleApiCall {
        client.post("auth/token") {
            bearerAuth(token)
        }.body()
    }
}
