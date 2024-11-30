package com.github.fpiechowski.kontaktio

enum class Environment(val value: String) {
    Local("local"),
    Docker("docker"),
    Staging("staging"),
    Production("production");

    companion object {
        fun fromSystem(): Environment =
            when (System.getenv("environment")) {
                Local.value -> Local
                Docker.value -> Docker
                Staging.value -> Staging
                Production.value -> Production
                else -> Local
            }
    }
}