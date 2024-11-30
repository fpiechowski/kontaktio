package com.github.fpiechowski.kontaktio

import arrow.core.raise.either
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.fp.getOrElse
import com.sksamuel.hoplite.sources.EnvironmentVariableOverridePropertySource

data class Config(val server: Server, val kontaktApi: KontaktApi) {
    data class Server(val port: Int)
    data class KontaktApi(val baseUrl: String, val apiKey: String)

    companion object {
        fun load(environment: Environment) =
            either {
                ConfigLoaderBuilder.default()
                    .addResourceSource("/config/${environment.value}.yaml")
                    .addSource(EnvironmentVariableOverridePropertySource(false))
                    .build()
                    .loadConfig<Config>()
                    .getOrElse {
                        raise(ConfigFailureError(it.description()))
                    }
            }
    }

    data class ConfigFailureError(
        override val message: String
    ) : KontaktError(message)
}
