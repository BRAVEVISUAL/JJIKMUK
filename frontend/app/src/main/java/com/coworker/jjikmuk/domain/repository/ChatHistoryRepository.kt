package com.coworker.jjikmuk.domain.repository

import com.coworker.jjikmuk.domain.model.ChatHistory
import com.coworker.jjikmuk.domain.model.ChatMessage

interface ChatHistoryRepository {
    fun getChatHistories(): List<ChatHistory>

    fun getChatHistory(historyId: Long): ChatHistory?

    fun upsertChatHistory(
        id: Long?,
        title: String,
        subtitle: String,
        lastMessageTime: String,
        messages: List<ChatMessage>
    ): ChatHistory

    fun togglePinChatHistory(historyId: Long)

    fun deleteChatHistory(historyId: Long)
}
