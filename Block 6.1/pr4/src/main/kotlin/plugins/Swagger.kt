package com.example.nobelapi.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSwagger() {
    routing {
        swaggerUI(path = "swagger")
    }
}