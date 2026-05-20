package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.domain.model.MealContext
import com.coworker.jjikmuk.domain.model.Product
import com.coworker.jjikmuk.domain.repository.ChatRepository
import com.coworker.jjikmuk.domain.repository.ProductRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val productRepository: ProductRepository
) : ChatRepository {

    override fun makeDummyResponse(userMessage: String, mealContext: MealContext): String {
        val participantText = mealContext.selectedProfiles
            .joinToString(", ") { profile -> profile.name }
            .ifBlank { "선택된 식사 참여자 없음" }
        val allergyText = mealContext.allergyNames
            .joinToString(", ")
            .ifBlank { "등록된 알레르기 없음" }

        return "'$userMessage'에 대해 확인해볼게요. 현재 같이 식사하는 사람은 $participantText 이고, 주의해야 할 알레르기는 $allergyText 입니다.\n\n입력한 음식명이나 제품명을 기준으로 성분, 알레르기 가능성, 섭취 시 주의할 점을 안내할 수 있습니다. 실제 서비스에서는 이 식사 참여자 컨텍스트를 챗봇 API 요청에 함께 전달하면 됩니다."
    }

    override fun getRecommendProducts(limit: Int): List<Product> {
        return productRepository.getRecommendProducts(limit)
    }
}
