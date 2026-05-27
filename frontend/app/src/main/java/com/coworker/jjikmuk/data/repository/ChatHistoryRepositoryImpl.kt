package com.coworker.jjikmuk.data.repository

import android.content.Context
import com.coworker.jjikmuk.domain.model.ChatHistory
import com.coworker.jjikmuk.domain.model.ChatMessage
import com.coworker.jjikmuk.domain.repository.ChatHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatHistoryRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : ChatHistoryRepository {

    private val preferences = context.getSharedPreferences(
        CHAT_HISTORY_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    override fun getChatHistories(): List<ChatHistory> {
        return readHistories()
    }

    override fun getChatHistory(historyId: Long): ChatHistory? {
        return readHistories().firstOrNull { history ->
            history.id == historyId
        }
    }

    override fun upsertChatHistory(
        id: Long?,
        title: String,
        subtitle: String,
        lastMessageTime: String,
        messages: List<ChatMessage>
    ): ChatHistory {
        val histories = readHistories()
        val existingHistory = histories.firstOrNull { history -> history.id == id }
        val historyId = existingHistory?.id ?: (id ?: System.currentTimeMillis())
        val updatedHistory = ChatHistory(
            id = historyId,
            title = title.ifBlank { DEFAULT_CHAT_TITLE },
            subtitle = subtitle.ifBlank { DEFAULT_CHAT_SUBTITLE },
            lastMessageTime = lastMessageTime,
            isPinned = existingHistory?.isPinned ?: false,
            messages = messages
        )
        val updatedHistories = buildList {
            add(updatedHistory)
            addAll(histories.filterNot { history -> history.id == historyId })
        }

        saveHistories(updatedHistories)

        return updatedHistory
    }

    override fun togglePinChatHistory(historyId: Long) {
        val histories = readHistories().map { history ->
            if (history.id == historyId) {
                history.copy(isPinned = !history.isPinned)
            } else {
                history
            }
        }

        saveHistories(histories)
    }

    override fun deleteChatHistory(historyId: Long) {
        val histories = readHistories().filterNot { history ->
            history.id == historyId
        }

        saveHistories(histories)
    }

    private fun readHistories(): List<ChatHistory> {
        val json = preferences.getString(KEY_CHAT_HISTORIES, null) ?: return emptyList()
        val type = object : TypeToken<List<ChatHistory>>() {}.type

        return runCatching {
            gson.fromJson<List<ChatHistory>>(json, type)
        }.getOrDefault(emptyList())
    }

    private fun saveHistories(histories: List<ChatHistory>) {
        preferences.edit()
            .putString(KEY_CHAT_HISTORIES, gson.toJson(histories))
            .apply()
    }

    companion object {
        private const val CHAT_HISTORY_PREFERENCES_NAME = "chat_history_preferences"
        private const val KEY_CHAT_HISTORIES = "chat_histories"
        private const val DEFAULT_CHAT_TITLE = "새 채팅"
        private const val DEFAULT_CHAT_SUBTITLE = "대화를 시작했어요."
    }
}
