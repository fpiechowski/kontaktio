package com.github.fpiechowski.kontaktio

import arrow.core.raise.either
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.fp.getOrElse

data class Config(val server: Server, val kontaktApi: KontaktApi) {
    data class Server(val port: Int)
    data class KontaktApi(val baseUrl: String, val apiKey: String)

    companion object {
        fun load(environment: Environment) =
            either {
                ConfigLoader().loadConfig<Config>("/config/${environment.value}.yaml")
                    .getOrElse {
                        raise(ConfigFailureError(it.description()))
                    }
            }
    }

    data class ConfigFailureError(
        override val message: String
    ) : KontaktError(message)
}
