package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.domain.model.ChatHistory
import com.coworker.jjikmuk.domain.repository.ChatHistoryRepository

class ChatHistoryRepositoryImpl : ChatHistoryRepository {

    override fun getChatHistories(): List<ChatHistory> {
        return listOf(
            ChatHistory(
                id = 1L,
                title = "Name",
                subtitle = "1번째 상품",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 2L,
                title = "Name",
                subtitle = "2번째 상품",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 3L,
                title = "Name",
                subtitle = "3번째 상품",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 4L,
                title = "Name",
                subtitle = "4번째 상품",
                lastMessageTime = "12:00am"
            ),
            ChatHistory(
                id = 5L,
                title = "Name",
                subtitle = "5번째 상품",
                lastMessageTime = "12:00am"
            )
        )
    }
}
