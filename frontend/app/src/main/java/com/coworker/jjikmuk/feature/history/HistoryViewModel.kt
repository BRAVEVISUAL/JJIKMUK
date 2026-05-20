package com.coworker.jjikmuk.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coworker.jjikmuk.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadFavoriteProducts() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val favoriteProducts = favoriteRepository.getFavoriteProducts()

            _uiState.update { state ->
                state.copy(
                    favoriteProducts = favoriteProducts,
                    unfavoritedProductIds = emptySet(),
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val isNowFavorite = favoriteRepository.toggleFavorite(productId)

            _uiState.update { state ->
                val updatedUnfavoritedProductIds = if (isNowFavorite) {
                    state.unfavoritedProductIds - productId
                } else {
                    state.unfavoritedProductIds + productId
                }

                state.copy(
                    unfavoritedProductIds = updatedUnfavoritedProductIds
                )
            }
        }
    }

    fun selectLikesTab() {
        _uiState.update { state ->
            state.copy(selectedTab = HistoryTab.LIKES)
        }
    }

    fun selectRecentlyViewedTab() {
        _uiState.update { state ->
            state.copy(selectedTab = HistoryTab.RECENTLY_VIEWED)
        }
    }
}