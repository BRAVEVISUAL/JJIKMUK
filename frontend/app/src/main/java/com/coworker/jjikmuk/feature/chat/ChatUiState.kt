package com.coworker.jjikmuk.feature.chat

import com.coworker.jjikmuk.domain.model.ChatMessage
import com.coworker.jjikmuk.domain.model.Product

data class ChatUiState(
    val title: String = "Chat",
    val messages: List<ChatMessage> = emptyList(),
    val recommendedProducts: List<Product> = emptyList(),
    val shouldShowRecommendSheet: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)