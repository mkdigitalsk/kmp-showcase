package com.mk.kmpshowcase.data.client

import com.mk.kmpshowcase.contracts.user.UserResponseDTO
import com.mk.kmpshowcase.data.network.handleApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface UserClient {
    suspend fun fetchUser(id: Long): UserResponseDTO
    suspend fun fetchUsers(): List<UserResponseDTO>
}

class UserClientImpl(
    private val client: HttpClient
) : UserClient {

    override suspend fun fetchUser(id: Long): UserResponseDTO = handleApiCall {
        client.get("users/$id").body()
    }

    override suspend fun fetchUsers(): List<UserResponseDTO> = handleApiCall {
        client.get("users").body()
    }
}
