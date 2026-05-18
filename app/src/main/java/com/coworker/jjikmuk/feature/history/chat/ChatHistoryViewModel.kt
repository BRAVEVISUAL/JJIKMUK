package com.coworker.jjikmuk.feature.history.chat

import androidx.lifecycle.ViewModel
import com.coworker.jjikmuk.data.repository.ChatHistoryRepositoryImpl
import com.coworker.jjikmuk.domain.model.ChatHistory
import com.coworker.jjikmuk.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ChatHistoryViewModel : ViewModel() {

    private val repository: ChatHistoryRepository = ChatHistoryRepositoryImpl()

    private val _uiState = MutableStateFlow(ChatHistoryUiState())
    val uiState: StateFlow<ChatHistoryUiState> = _uiState

    init {
        loadChatHistories()
    }

    fun loadChatHistories() {
        val histories = repository.getChatHistories()
        _uiState.update { state ->
            state.copy(
                histories = histories,
                filteredHistories = filterHistories(histories, state.searchQuery)
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredHistories = filterHistories(state.histories, query)
            )
        }
    }

    fun toggleEditMode() {
        _uiState.update { state ->
            state.copy(isEditMode = !state.isEditMode)
        }
    }

    private fun filterHistories(
        histories: List<ChatHistory>,
        query: String
    ): List<ChatHistory> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return histories

        return histories.filter { history ->
            history.title.contains(trimmedQuery, ignoreCase = true) ||
                history.subtitle.contains(trimmedQuery, ignoreCase = true)
        }
    }
}
