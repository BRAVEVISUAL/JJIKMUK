package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.domain.model.Product
import com.coworker.jjikmuk.domain.repository.ProductRepository
import com.coworker.jjikmuk.feature.chat.model.RecommendProduct
import com.coworker.jjikmuk.feature.product.dummy.ProductDummyData

class ProductRepositoryImpl : ProductRepository {

    override fun getAllProducts(): List<Product> {
        return ProductDummyData.recommendProducts.map { recommendProduct ->
            recommendProduct.toProduct()
        }
    }

    override fun getRecommendProducts(): List<Product> {
        return getAllProducts()
    }

    override fun getRecommendProducts(limit: Int): List<Product> {
        return getAllProducts().take(limit)
    }

    override fun findProductById(productId: String): Product? {
        return ProductDummyData.findProductById(productId)?.toProduct()
    }

    private fun RecommendProduct.toProduct(): Product {
        return Product(
            id = id,
            category = category,
            name = name,
            imageResId = imageResId,
            allergyTags = allergyTags
        )
    }
}