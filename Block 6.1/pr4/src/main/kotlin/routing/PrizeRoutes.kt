package com.example.nobelapi.routing
import com.example.nobelapi.di.AppModule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.prizeRoutes(appModule: AppModule) {
    get("/prizes") {
        val prizes = appModule.getPrizesUseCase()
        call.respond(prizes)
    }
    authenticate("auth-jwt") {
        get("/prizes/{year}/{category}") {
            val year = call.parameters["year"] ?: throw IllegalArgumentException("Missing year")
            val category = call.parameters["category"] ?: throw IllegalArgumentException("Missing category")
            val prize = appModule.getPrizeByYearAndCategoryUseCase(year, category)
            if (prize != null) {
                call.respond(prize)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Prize not found"))
            }
        }

        get("/prizes/{year}/{category}/laureates") {
            val year = call.parameters["year"] ?: throw IllegalArgumentException("Missing year")
            val category = call.parameters["category"] ?: throw IllegalArgumentException("Missing category")
            val laureates = appModule.getLaureatesByPrizeUseCase(year, category)
            if (laureates != null) {
                call.respond(laureates)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Laureates not found"))
            }
        }

        post("/admin/load-prizes") {
            val count = appModule.loadNobelPrizesUseCase()
            call.respond(mapOf("message" to "Loaded $count prizes from Nobel API"))
        }
    }
}