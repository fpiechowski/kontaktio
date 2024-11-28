package com.github.fpiechowski.kontaktio

open class KontaktError(open val message: String, val cause: Error? = null, val throwable: Throwable? = null)