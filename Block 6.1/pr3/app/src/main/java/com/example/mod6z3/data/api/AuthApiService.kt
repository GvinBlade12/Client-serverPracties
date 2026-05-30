package com.example.mod6z3.data.api
import com.example.mod6z3.data.dto.LoginRequest
import com.example.mod6z3.data.dto.LoginResponse
import com.example.mod6z3.data.dto.UsersResponse
import com.example.mod6z3.data.dto.UserDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post {
            url("https://dummyjson.com/auth/login")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getUsers(token: String): UsersResponse {
        return client.get {
            url("https://dummyjson.com/users")
            header("Authorization", token)
        }.body()
    }

    suspend fun getUserById(token: String, userId: Int): UserDto {
        return client.get {
            url("https://dummyjson.com/users/$userId")
            header("Authorization", token)
        }.body()
    }
}