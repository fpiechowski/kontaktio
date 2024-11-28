package com.github.fpiechowski.kontaktio

data class Building(
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