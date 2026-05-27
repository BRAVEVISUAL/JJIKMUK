package com.coworker.jjikmuk.domain.model

data class ChatResponse(
    val answer: String,
    val productCandidates: List<ChatProductCandidate> = emptyList()
)

data class ChatProductCandidate(
    val productName: String,
    val barcode: String,
    val category: String,
    val rawMaterials: String,
    val allergy: List<String> = emptyList(),
    val allergyInfo: String? = null,
    val nutrition: String? = null
)
