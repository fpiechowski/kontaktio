package com.github.fpiechowski.kontaktio

import arrow.continuations.SuspendApp
import arrow.core.raise.recover
import arrow.fx.coroutines.resourceScope
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.awaitCancellation

fun main() = SuspendApp {
    val logger = KotlinLogging.logger("Main")

    recover<KontaktError, Unit>(block = {
        val environment = Environment.fromSystem()
        val config = Config.load(environment).bind()

        resourceScope {
            install({
                embeddedServer(Netty, config.server.port) {
                    healthCheck()
                }.start()
            }) { engine, _ ->
                engine.stop(gracePeriodMillis = 1000, timeoutMillis = 1000)
            }

            awaitCancellation()
        }
    }, recover = {
        logger.error {
            """${it::class.simpleName}: ${it.message}
                |${it.cause?.let { "Caused by: ${it::class.simpleName}: ${it.message}" } ?: ""}
                |${
                it.throwable?.let { "With throwable: ${it::class.simpleName}: ${it.message}".trimMargin() } ?: ""
            }
            """.trimMargin()
        }

        throw Exception("Exiting due to error ${it::class.simpleName}: ${it.message}")
    }, catch = {
        logger.error {
            """${it::class.simpleName}: ${it.message}
                |${it.cause?.let { "Caused by: ${it::class.simpleName}: ${it.message}" } ?: ""}  
            """.trimMargin()
        }

        throw Exception("Exiting due to exception ${it::class.simpleName}: ${it.message}")
    })
}
