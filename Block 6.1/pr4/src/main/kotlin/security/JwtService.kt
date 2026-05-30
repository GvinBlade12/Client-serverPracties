package com.example.nobelapi.security
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService {
    private val secret = "my-super-secret-key-for-jwt-that-is-at-least-32-chars-long!!"
    private val issuer = "nobel-api"
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun generateToken(username: String): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 30 * 60 * 1000))
            .sign(algorithm)
    }
}