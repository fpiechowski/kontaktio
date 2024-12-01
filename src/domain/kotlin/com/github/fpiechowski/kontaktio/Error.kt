package com.github.fpiechowski.kontaktio

open class KontaktError(
    open val message: String,
    open val cause: KontaktError? = null,
    open val throwable: Throwable? = null
)
