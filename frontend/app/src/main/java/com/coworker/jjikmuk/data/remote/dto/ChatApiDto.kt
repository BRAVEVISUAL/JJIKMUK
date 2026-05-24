package com.coworker.jjikmuk.data.remote.dto

data class ChatApiRequest(
    val profiles: List<ChatApiProfile>,
    val product: ChatApiProduct? = null,
    val message: String,
    val chat_history: List<ChatApiHistoryMessage> = emptyList()
)

data class ChatApiProfile(
    val id: Int,
    val nickname: String,
    val allergies: List<String>,
    val targetType: String,
    val isActive: Boolean
)

data class ChatApiProduct(
    val productName: String,
    val barcode: String,
    val allergy: List<String>,
    val rawMaterials: String
)

data class ChatApiHistoryMessage(
    val role: String,
    val content: String
)

data class ChatApiResponse(
    val task_type: String? = null,
    val answer_source: String? = null,
    val pipeline_stage: String? = null,
    val intent: String? = null,
    val answer: String,
    val risk_level: String? = null,
    val reasons: List<String> = emptyList(),
    val profile_name: String? = null,
    val active_profile_names: List<String> = emptyList(),
    val recommended_questions: List<String> = emptyList(),
    val recommended_products: List<ChatApiRecommendedProduct> = emptyList()
)

data class ChatApiRecommendedProduct(
    val product_name: String? = null,
    val barcode: String? = null,
    val reason: String? = null
)