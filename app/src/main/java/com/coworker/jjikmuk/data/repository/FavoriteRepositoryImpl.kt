package com.coworker.jjikmuk.data.repository

import android.content.Context
import com.coworker.jjikmuk.data.local.preference.FavoriteProductStore
import com.coworker.jjikmuk.domain.model.Product
import com.coworker.jjikmuk.domain.repository.FavoriteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FavoriteRepository {

    override suspend fun isFavorite(productId: String): Boolean {
        return FavoriteProductStore.isFavorite(context, productId)
    }

    override suspend fun toggleFavorite(productId: String): Boolean {
        return FavoriteProductStore.toggleFavorite(context, productId)
    }

    override suspend fun getFavoriteProducts(): List<Product> {
        return FavoriteProductStore.getFavoriteProducts(context)
    }
}
