package com.example.nobelapi
import com.example.nobelapi.data.database.DatabaseFactory
import com.example.nobelapi.di.AppModule
import com.example.nobelapi.plugins.*
import com.example.nobelapi.routing.authRoutes
import com.example.nobelapi.routing.prizeRoutes
import com.example.nobelapi.routing.userRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val appModule = AppModule()

    configureContentNegotiation()
    configureCORS()
    configureCallLogging()
    configureStatusPages()
    configureAuthentication(appModule.jwtService)
    configureSwagger()

    routing {
        authRoutes(appModule)
        prizeRoutes(appModule)
        userRoutes(appModule)
    }
}