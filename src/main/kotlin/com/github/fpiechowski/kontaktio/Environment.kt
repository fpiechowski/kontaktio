package com.github.fpiechowski.kontaktio

enum class Environment(val value: String) {
    Local("local"),
    Docker("docker"),
    Development("development"),
    Test("test"),
    Staging("staging"),
    Production("production");

    companion object {
        fun fromSystem(): Environment =
            when (System.getenv("environment")) {
                Local.value -> Local
                Docker.value -> Docker
                Development.value -> Development
                Test.value -> Test
                Staging.value -> Staging
                Production.value -> Production
                else -> Local
            }
    }
}