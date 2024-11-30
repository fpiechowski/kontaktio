package com.github.fpiechowski.kontaktio

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import kotlinx.serialization.json.Json
import java.util.*

fun Application.plugins(json: Json) {
    install(Resources)
    install(ContentNegotiation) {
        json(json)
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { "KONTAKTIO-${UUID.randomUUID()}" }
    }
    install(CallLogging) {
        callIdMdc("call-id")
    }
}