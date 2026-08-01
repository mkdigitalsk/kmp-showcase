package com.mk.kmpshowcase.data.client

import com.mk.kmpshowcase.contracts.auth.AuthResponseDTO
import com.mk.kmpshowcase.contracts.auth.LoginRequestDTO
import com.mk.kmpshowcase.contracts.auth.RegisterRequestDTO
import com.mk.kmpshowcase.data.network.handleApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface AuthClient {
    suspend fun login(email: String, password: String): AuthResponseDTO
    suspend fun register(email: String, password: String, name: String): AuthResponseDTO
    suspend fun me(token: String): AuthResponseDTO
}

class AuthClientImpl(
    private val client: HttpClient
) : AuthClient {

    override suspend fun login(email: String, password: String): AuthResponseDTO = handleApiCall {
        client.post("auth/login") {
            setBody(LoginRequestDTO(email, password))
        }.body()
    }

    override suspend fun register(email: String, password: String, name: String): AuthResponseDTO = handleApiCall {
        client.post("auth/register") {
            setBody(RegisterRequestDTO(email, password, name))
        }.body()
    }

    override suspend fun me(token: String): AuthResponseDTO = handleApiCall {
        client.get("auth/me") {
            bearerAuth(token)
        }.body()
    }
}
