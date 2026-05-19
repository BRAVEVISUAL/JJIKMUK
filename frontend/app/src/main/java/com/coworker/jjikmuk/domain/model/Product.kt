package com.coworker.jjikmuk.domain.model

data class Product(
    val id: String,
    val category: String,
    val name: String,
    val allergyTags: List<String> = emptyList()
)