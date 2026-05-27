package com.coworker.jjikmuk.feature.chat

import com.coworker.jjikmuk.domain.model.ChatMessage
import com.coworker.jjikmuk.domain.model.ChatProductCandidate

data class ChatUiState(
    val title: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val productCandidates: List<ChatProductCandidate> = emptyList(),
    val productCandidateSheetTitle: String = "원하시는 상품을 선택해주세요",
    val shouldShowRecommendSheet: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
