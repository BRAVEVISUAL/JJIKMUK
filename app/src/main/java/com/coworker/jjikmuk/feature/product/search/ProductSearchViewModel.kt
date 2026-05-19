package com.coworker.jjikmuk.feature.product.search

import androidx.lifecycle.ViewModel
import com.coworker.jjikmuk.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ProductSearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductSearchUiState())
    val uiState: StateFlow<ProductSearchUiState> = _uiState.asStateFlow()

    fun loadProducts() {
        _uiState.update { state ->
            state.copy(isLoading = true, errorMessage = null)
        }

        val products = productRepository.getAllProducts()

        _uiState.update { state ->
            state.copy(
                products = products,
                isLoading = false,
                errorMessage = null
            )
        }
    }
}
