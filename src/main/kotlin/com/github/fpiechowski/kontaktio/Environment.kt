package com.github.fpiechowski.kontaktio

enum class Environment(val value: String) {
    Local("local"),
    Staging("staging"),
    Production("production");

    companion object {
        fun fromSystem(): Environment =
            when (System.getenv("environment")) {
                Local.value -> Local
                Staging.value -> Staging
                Production.value -> Production
                else -> Local
            }
    }
}