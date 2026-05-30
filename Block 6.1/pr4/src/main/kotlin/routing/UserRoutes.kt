package com.example.nobelapi.routing

import com.example.nobelapi.di.AppModule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(appModule: AppModule) {
    authenticate("auth-jwt") {
        get("/users/me") {
            val principal = call.principal<UserIdPrincipal>()
            val username = principal?.name
            if (username == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@get
            }
            call.respondText("""{"id":1,"username":"$username","role":"admin"}""", ContentType.Application.Json)
        }

        get("/users/me/prizes") {
            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@get
            }
            call.respondText("[]", ContentType.Application.Json)
        }

        post("/users/me/prizes/{prizeId}") {
            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@post
            }
            call.respondText("""{"message":"Prize added to favorites"}""", ContentType.Application.Json)
        }

        delete("/users/me/prizes/{prizeId}") {
            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@delete
            }
            call.respondText("""{"message":"Prize removed from favorites"}""", ContentType.Application.Json)
        }
    }
}