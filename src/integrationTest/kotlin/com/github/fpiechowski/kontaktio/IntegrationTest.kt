package com.github.fpiechowski.kontaktio

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class IntegrationTest : FreeSpec({

    "health check" - {
        val response = httpClient.get("http://localhost:8080/health-check")

        "responds 200 OK" {
            response.shouldHaveStatus(200)
            response.bodyAsText() shouldBe "OK"
        }
    }
}) {
    companion object {
        val httpClient = HttpClient()
    }
}