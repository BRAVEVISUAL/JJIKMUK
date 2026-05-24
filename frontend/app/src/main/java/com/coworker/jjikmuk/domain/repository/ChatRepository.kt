package com.coworker.jjikmuk.domain.repository

import com.coworker.jjikmuk.domain.model.MealContext
import com.coworker.jjikmuk.domain.model.Product

interface ChatRepository {

    suspend fun sendMessage(
        userMessage: String,
        mealContext: MealContext
    ): String

    fun getRecommendProducts(limit: Int): List<Product>
}