package com.coworker.jjikmuk.feature.history

import com.coworker.jjikmuk.domain.model.Product

data class HistoryUiState(
    val favoriteProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: HistoryTab = HistoryTab.LIKES,
    val unfavoritedProductIds: Set<String> = emptySet()
)

enum class HistoryTab {
    LIKES,
    RECENTLY_VIEWED
}