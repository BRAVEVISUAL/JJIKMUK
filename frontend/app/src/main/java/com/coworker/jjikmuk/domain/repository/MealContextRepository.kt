package com.coworker.jjikmuk.domain.repository

import com.coworker.jjikmuk.domain.model.MealContext
import kotlinx.coroutines.flow.StateFlow

interface MealContextRepository {
    val mealContext: StateFlow<MealContext>

    fun setSelectedProfileIds(profileIds: Set<String>)
}
