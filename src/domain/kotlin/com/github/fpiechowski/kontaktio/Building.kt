package com.github.fpiechowski.kontaktio

import arrow.core.Either

data class Building(
    val id: Id,
    val name: String,
    val address: String,
    val floors: List<Floor>
) {
    data class Floor(
        val level: Int,
        val image: String,
        val properties: Map<String, String>
    )
}

typealias Id = Long
typealias GetBuilding = suspend (Id) -> Either<BuildingError, Building>

open class BuildingError(
    override val message: String,
    override val cause: KontaktError? = null,
    override val throwable: Throwable? = null
) : KontaktError(message, cause, throwable)
