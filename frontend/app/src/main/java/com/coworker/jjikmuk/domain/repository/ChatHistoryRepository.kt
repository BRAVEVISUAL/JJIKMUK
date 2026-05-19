package com.coworker.jjikmuk.domain.repository

import com.coworker.jjikmuk.domain.model.ChatHistory

interface ChatHistoryRepository {
    fun getChatHistories(): List<ChatHistory>
}
