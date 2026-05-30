package com.example.nobelapi.plugins

import com.example.nobelapi.security.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication(jwtService: JwtService) {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(jwtService.verifier)
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != null) {
                    UserIdPrincipal(credential.payload.getClaim("username").asString())
                } else null
            }
        }
    }
}