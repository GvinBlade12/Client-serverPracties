package com.example.nobelapi.routing

import com.example.nobelapi.di.AppModule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

fun Route.authRoutes(appModule: AppModule) {
    post("/auth/login") {
        val receive = call.receive<LoginRequest>()
        val isValid = appModule.validateUserUseCase(receive.username, receive.password)
        if (isValid) {
            val token = appModule.jwtService.generateToken(receive.username)
            call.respond(mapOf("token" to token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
    }
}