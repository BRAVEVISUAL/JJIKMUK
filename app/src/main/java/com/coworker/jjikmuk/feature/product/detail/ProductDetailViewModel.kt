package com.coworker.jjikmuk.feature.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coworker.jjikmuk.domain.repository.FavoriteRepository
import com.coworker.jjikmuk.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var currentProductId: String = ""

    fun loadProduct(productId: String) {
        currentProductId = productId

        _uiState.update { state ->
            state.copy(isLoading = true, errorMessage = null)
        }

        val product = productRepository.findProductById(productId)

        if (product == null) {
            _uiState.update { state ->
                state.copy(
                    product = null,
                    isFavorite = false,
                    isLoading = false,
                    errorMessage = "상품을 찾을 수 없습니다."
                )
            }
            return
        }

        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(productId)

            _uiState.update { state ->
                state.copy(
                    product = product,
                    isFavorite = isFavorite,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun toggleFavorite() {
        val productId = currentProductId
        if (productId.isBlank()) return

        viewModelScope.launch {
            val isNowFavorite = favoriteRepository.toggleFavorite(productId)

            _uiState.update { state ->
                state.copy(isFavorite = isNowFavorite)
            }
        }
    }
}
