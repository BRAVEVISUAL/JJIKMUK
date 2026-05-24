package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.data.remote.api.ChatApi
import com.coworker.jjikmuk.data.remote.dto.ChatApiProfile
import com.coworker.jjikmuk.data.remote.dto.ChatApiRequest
import com.coworker.jjikmuk.domain.model.MealContext
import com.coworker.jjikmuk.domain.model.Product
import com.coworker.jjikmuk.domain.repository.ChatRepository
import com.coworker.jjikmuk.domain.repository.ProductRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val productRepository: ProductRepository,
    private val chatApi: ChatApi
) : ChatRepository {

    override suspend fun sendMessage(
        userMessage: String,
        mealContext: MealContext
    ): String {
        return runCatching {
            val request = ChatApiRequest(
                profiles = mealContext.toChatApiProfiles(),
                product = null,
                message = userMessage,
                chat_history = emptyList()
            )

            chatApi.sendMessage(request).answer
        }.getOrElse {
            makeFallbackResponse(userMessage, mealContext)
        }
    }

    override fun getRecommendProducts(limit: Int): List<Product> {
        return productRepository.getRecommendProducts(limit)
    }

    private fun MealContext.toChatApiProfiles(): List<ChatApiProfile> {
        return selectedProfiles.mapIndexed { index, profile ->
            ChatApiProfile(
                id = index + 1,
                nickname = profile.name,
                allergies = profile.allergies,
                targetType = if (profile.id == "me") "self" else "family",
                isActive = true
            )
        }
    }

    private fun makeFallbackResponse(
        userMessage: String,
        mealContext: MealContext
    ): String {
        val participantText = mealContext.selectedProfiles
            .joinToString(", ") { profile -> profile.name }
            .ifBlank { "선택된 식사 참여자 없음" }

        val allergyText = mealContext.allergyNames
            .joinToString(", ")
            .ifBlank { "등록된 알레르기 없음" }

        return "'$userMessage'에 대해 확인해볼게요. 현재 같이 식사하는 사람은 $participantText 이고, 주의해야 할 알레르기는 $allergyText 입니다.\n\n현재 AI 서버 연결에 실패해서 임시 안내로 보여드리고 있어요."
    }
}