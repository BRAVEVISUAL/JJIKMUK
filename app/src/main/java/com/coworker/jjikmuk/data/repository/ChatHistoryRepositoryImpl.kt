package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.domain.model.ChatHistory
import com.coworker.jjikmuk.domain.repository.ChatHistoryRepository

class ChatHistoryRepositoryImpl : ChatHistoryRepository {

    override fun getChatHistories(): List<ChatHistory> {
        return listOf(
            ChatHistory(
                id = 1L,
                title = "Rick Astley",
                subtitle = "Secondary line of text - 12:00am",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 2L,
                title = "Name",
                subtitle = "Secondary line of text - 12:00am",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 3L,
                title = "Name",
                subtitle = "Secondary line of text - 12:00am",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 4L,
                title = "Name",
                subtitle = "Secondary line of text - 12:00am",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 5L,
                title = "Name",
                subtitle = "Secondary line of text - 12:00am",
                lastMessageTime = "12:00am"
            )
        )
    }
}
