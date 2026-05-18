package com.coworker.jjikmuk.feature.history.chat

import com.coworker.jjikmuk.domain.model.ChatHistory

data class ChatHistoryUiState(
    val searchQuery: String = "",
    val histories: List<ChatHistory> = emptyList(),
    val filteredHistories: List<ChatHistory> = emptyList(),
    val isEditMode: Boolean = false
)
