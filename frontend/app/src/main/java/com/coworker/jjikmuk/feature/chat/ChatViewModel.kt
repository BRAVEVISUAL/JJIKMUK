package com.coworker.jjikmuk.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coworker.jjikmuk.domain.model.ChatMessage
import com.coworker.jjikmuk.domain.model.UploadOption
import com.coworker.jjikmuk.domain.repository.ChatRepository
import com.coworker.jjikmuk.domain.repository.MealContextRepository
import com.coworker.jjikmuk.feature.product.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val mealContextRepository: MealContextRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _uploadOptionEvent = MutableSharedFlow<UploadOption>()
    val uploadOptionEvent: SharedFlow<UploadOption> = _uploadOptionEvent

    private var nextMessageId: Long = 0L

    fun startChat(initialMessage: String) {
        val trimmedMessage = initialMessage.trim()
        if (trimmedMessage.isBlank()) return

        val mealContext = mealContextRepository.mealContext.value
        val userMessage = createUserMessage(trimmedMessage)
        val recommendProducts = getRecommendProductUiModels()

        _uiState.update { state ->
            state.copy(
                title = makeTitle(trimmedMessage),
                messages = listOf(userMessage),
                recommendedProducts = recommendProducts,
                shouldShowRecommendSheet = false,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val botAnswer = chatRepository.sendMessage(trimmedMessage, mealContext)
            val botMessage = createBotMessage(botAnswer)

            _uiState.update { state ->
                state.copy(
                    messages = state.messages + botMessage,
                    recommendedProducts = recommendProducts,
                    shouldShowRecommendSheet = true,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun sendMessage(message: String) {
        val trimmedMessage = message.trim()
        if (trimmedMessage.isBlank()) return

        val mealContext = mealContextRepository.mealContext.value
        val userMessage = createUserMessage(trimmedMessage)
        val recommendProducts = getRecommendProductUiModels()

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                recommendedProducts = recommendProducts,
                shouldShowRecommendSheet = false,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val botAnswer = chatRepository.sendMessage(trimmedMessage, mealContext)
            val botMessage = createBotMessage(botAnswer)

            _uiState.update { state ->
                state.copy(
                    messages = state.messages + botMessage,
                    recommendedProducts = recommendProducts,
                    shouldShowRecommendSheet = true,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun onRecommendSheetShown() {
        _uiState.update { state ->
            state.copy(shouldShowRecommendSheet = false)
        }
    }

    fun onUploadOptionSelected(option: UploadOption) {
        viewModelScope.launch {
            _uploadOptionEvent.emit(option)
        }
    }

    private fun getRecommendProductUiModels() =
        chatRepository.getRecommendProducts(limit = 2)
            .map { product ->
                product.toUiModel()
            }

    private fun createUserMessage(message: String): ChatMessage {
        return ChatMessage(
            id = nextMessageId++,
            text = message,
            senderType = ChatMessage.SenderType.USER
        )
    }

    private fun createBotMessage(message: String): ChatMessage {
        return ChatMessage(
            id = nextMessageId++,
            text = message,
            senderType = ChatMessage.SenderType.BOT
        )
    }

    private fun makeTitle(message: String): String {
        return if (message.length > 12) {
            message.take(12) + ".."
        } else {
            message
        }
    }
}