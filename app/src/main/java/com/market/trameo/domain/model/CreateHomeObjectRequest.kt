package com.market.trameo.domain.model

data class CreateHomeObjectRequest(
    val photos: List<String>,
    val name: String,
    val description: String,
    val category: String,
    val condition: String,
    val exchangePreferences: String
)

