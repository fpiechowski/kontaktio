package com.github.fpiechowski.kontaktio

import arrow.continuations.SuspendApp
import arrow.core.raise.recover
import arrow.fx.coroutines.resourceScope
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

fun main() = SuspendApp {
    val logger = KotlinLogging.logger("Main")

    recover<KontaktError, Unit>(
        block = {
            val environment = Environment.fromSystem()
            val config = Config.load(environment).bind()
            val json = Json {
                ignoreUnknownKeys = true
            }
            val httpClient = HttpClient {
                install(Logging) {
                    level = LogLevel.BODY
                }
                install(ClientContentNegotiation) {
                    json(json)
                }
            }

            resourceScope {
                install({
                    embeddedServer(Netty, config.server.port) {
                        install(Resources)
                        install(ServerContentNegotiation) {
                            json(json)
                        }

                        healthCheck()
                        building(getBuilding(httpClient, config), BuildingResponse.Factory.withDecodedImage(json))
                    }.start()
                }) { engine, _ ->
                    engine.stop(gracePeriodMillis = 1000, timeoutMillis = 1000)
                }

                awaitCancellation()
            }
        },
        recover = {
            logger.logError(it)
            throw Exception("Exiting due to error ${it::class.simpleName}: ${it.message}")
        },
        catch = {
            logger.logThrowable(it)
            throw Exception("Exiting due to exception ${it::class.simpleName}: ${it.message}")
        }
    )
}

private fun KLogger.logError(kontaktError: KontaktError) {
    error {
        """${kontaktError::class.simpleName}: ${kontaktError.message}
                |${kontaktError.cause?.let { "Caused by: ${it::class.simpleName}: ${it.message}" } ?: ""}
                |${
            kontaktError.throwable?.let { "With throwable: ${it::class.simpleName}: ${it.message}".trimMargin() } ?: ""
        }
            """.trimMargin()
    }
}

private fun KLogger.logThrowable(throwable: Throwable) {
    error {
        """${throwable::class.simpleName}: ${throwable.message}
                |${throwable.cause?.let { "Caused by: ${it::class.simpleName}: ${it.message}" } ?: ""}  
            """.trimMargin()
    }
}
