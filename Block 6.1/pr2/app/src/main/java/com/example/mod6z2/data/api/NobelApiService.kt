package com.example.mod6z2.data.api
import com.example.mod6z2.data.dto.LoginRequest
import com.example.mod6z2.data.dto.LoginResponse
import com.example.mod6z2.data.dto.NobelPrizeDto
import com.example.mod6z2.data.dto.LaureateDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class NobelApiService(private val client: HttpClient) {

    suspend fun login(username: String, password: String): LoginResponse {
        return client.post {
            url("http://10.0.2.2:8080/auth/login")
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }.body()
    }

    suspend fun getPrizes(token: String): List<NobelPrizeDto> {
        return client.get {
            url("http://10.0.2.2:8080/prizes")
            header("Authorization", token)
        }.body()
    }

    suspend fun getPrizeDetail(token: String, year: String, category: String): NobelPrizeDto {
        return client.get {
            url("http://10.0.2.2:8080/prizes/$year/$category")
            header("Authorization", token)
        }.body()
    }

    suspend fun getLaureates(token: String, year: String, category: String): List<LaureateDto> {
        return client.get {
            url("http://10.0.2.2:8080/prizes/$year/$category/laureates")
            header("Authorization", token)
        }.body()
    }
}