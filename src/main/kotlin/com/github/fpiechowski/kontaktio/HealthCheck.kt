package com.github.fpiechowski.kontaktio

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.healthCheck() {
    routing {
        get("/health-check") {
            call.respond("OK")
        }
    }
}
