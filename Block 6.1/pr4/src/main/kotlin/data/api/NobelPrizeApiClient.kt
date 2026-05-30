package com.example.nobelapi.data.api
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class NobelPrizeApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun fetchNobelPrizes(): NobelPrizeApiResponse {
        return client.get {
            url("https://api.nobelprize.org/2.1/nobelPrizes?limit=50")
        }.body()
    }
}

@Serializable
data class NobelPrizeApiResponse(val nobelPrizes: List<NobelPrizeApiDto>)

@Serializable
data class NobelPrizeApiDto(
    val awardYear: String,
    val category: CategoryApiDto,
    val laureates: List<LaureateApiDto>? = null
)

@Serializable
data class CategoryApiDto(val en: String)

@Serializable
data class LaureateApiDto(
    val id: String,
    val fullName: FullNameApiDto? = null,
    val motivation: MotivationApiDto? = null
)

@Serializable
data class FullNameApiDto(val en: String? = null)

@Serializable
data class MotivationApiDto(val en: String? = null)


