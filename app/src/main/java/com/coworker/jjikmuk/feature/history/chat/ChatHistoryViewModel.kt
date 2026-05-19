package com.coworker.jjikmuk.feature.history.chat

import androidx.lifecycle.ViewModel
import com.coworker.jjikmuk.domain.model.ChatHistory
import com.coworker.jjikmuk.domain.repository.ChatHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    private val repository: ChatHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatHistoryUiState())
    val uiState: StateFlow<ChatHistoryUiState> = _uiState.asStateFlow()

    init {
        loadChatHistories()
    }

    fun loadChatHistories() {
        val histories = sortHistories(repository.getChatHistories())
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

    fun pinChatHistory(historyId: Long) {
        _uiState.update { state ->
            val histories = state.histories.map { history ->
                if (history.id == historyId) {
                    history.copy(isPinned = !history.isPinned)
                } else {
                    history
                }
            }.let(::sortHistories)

            state.copy(
                histories = histories,
                filteredHistories = filterHistories(histories, state.searchQuery)
            )
        }
    }

    fun deleteChatHistory(historyId: Long) {
        _uiState.update { state ->
            val histories = state.histories.filterNot { history ->
                history.id == historyId
            }

            state.copy(
                histories = histories,
                filteredHistories = filterHistories(histories, state.searchQuery)
            )
        }
    }

    private fun sortHistories(histories: List<ChatHistory>): List<ChatHistory> {
        return histories.sortedWith(
            compareByDescending<ChatHistory> { history -> history.isPinned }
                .thenBy { history -> history.id }
        )
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
