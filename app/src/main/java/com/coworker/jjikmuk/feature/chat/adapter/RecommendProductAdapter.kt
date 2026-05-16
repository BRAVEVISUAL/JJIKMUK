package com.coworker.jjikmuk.feature.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.domain.model.Product

class RecommendProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, RecommendProductAdapter.RecommendProductViewHolder>(
    RecommendProductDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommend_product, parent, false)

        return RecommendProductViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RecommendProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecommendProductViewHolder(
        itemView: View,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvProductCategory: TextView =
            itemView.findViewById(R.id.tvProductCategory)

        private val tvProductName: TextView =
            itemView.findViewById(R.id.tvProductName)

        private val tvAllergyTag1: TextView =
            itemView.findViewById(R.id.tvAllergyTag1)

        private val tvAllergyTag2: TextView =
            itemView.findViewById(R.id.tvAllergyTag2)

        private val ivProductImage: ImageView =
            itemView.findViewById(R.id.ivProductImage)

        fun bind(product: Product) {
            tvProductCategory.text = product.category
            tvProductName.text = product.name
            ivProductImage.setImageResource(product.imageResId)

            val firstTag = product.allergyTags.getOrNull(0)
            val secondTag = product.allergyTags.getOrNull(1)

            if (firstTag != null) {
                tvAllergyTag1.visibility = View.VISIBLE
                tvAllergyTag1.text = firstTag
            } else {
                tvAllergyTag1.visibility = View.GONE
            }

            if (secondTag != null) {
                tvAllergyTag2.visibility = View.VISIBLE
                tvAllergyTag2.text = secondTag
            } else {
                tvAllergyTag2.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    private class RecommendProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }
    }
}