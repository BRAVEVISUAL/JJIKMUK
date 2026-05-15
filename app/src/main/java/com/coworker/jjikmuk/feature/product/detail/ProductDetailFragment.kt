package com.coworker.jjikmuk.feature.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.core.navigation.BottomNavController
import com.coworker.jjikmuk.data.local.preference.FavoriteProductStore
import com.coworker.jjikmuk.feature.product.dummy.ProductDummyData
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var currentProductId: String = ""
    private var btnProductFavorite: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentProductId = arguments?.getString(ARG_PRODUCT_ID).orEmpty()
        val product = ProductDummyData.findProductById(currentProductId)

        val btnProductDetailBack = view.findViewById<ImageButton>(R.id.btnProductDetailBack)
        val ivProductDetailImage = view.findViewById<ImageView>(R.id.ivProductDetailImage)
        val tvProductDetailCategory = view.findViewById<TextView>(R.id.tvProductDetailCategory)
        val tvProductDetailName = view.findViewById<TextView>(R.id.tvProductDetailName)
        val tvDetailAllergyTag1 = view.findViewById<TextView>(R.id.tvDetailAllergyTag1)
        val tvDetailAllergyTag2 = view.findViewById<TextView>(R.id.tvDetailAllergyTag2)
        btnProductFavorite = view.findViewById(R.id.btnProductFavorite)

        btnProductDetailBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        BottomNavController.bind(view, parentFragmentManager, requireContext())

        if (product == null) {
            tvProductDetailCategory.text = "상품 정보 없음"
            tvProductDetailName.text = "상품을 찾을 수 없습니다."
            tvDetailAllergyTag1.visibility = View.GONE
            tvDetailAllergyTag2.visibility = View.GONE
            btnProductFavorite?.visibility = View.GONE
            return
        }

        ivProductDetailImage.setImageResource(product.imageResId)
        tvProductDetailCategory.text = product.category
        tvProductDetailName.text = product.name

        val firstTag = product.allergyTags.getOrNull(0)
        val secondTag = product.allergyTags.getOrNull(1)

        if (firstTag != null) {
            tvDetailAllergyTag1.visibility = View.VISIBLE
            tvDetailAllergyTag1.text = firstTag
        } else {
            tvDetailAllergyTag1.visibility = View.GONE
        }

        if (secondTag != null) {
            tvDetailAllergyTag2.visibility = View.VISIBLE
            tvDetailAllergyTag2.text = secondTag
        } else {
            tvDetailAllergyTag2.visibility = View.GONE
        }

        updateFavoriteIcon()

        btnProductFavorite?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                FavoriteProductStore.toggleFavorite(requireContext(), currentProductId)
                updateFavoriteIcon()
            }
        }
    }

    private fun updateFavoriteIcon() {
        viewLifecycleOwner.lifecycleScope.launch {
            val iconResId = if (FavoriteProductStore.isFavorite(requireContext(), currentProductId)) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart_outline
            }

            btnProductFavorite?.setImageResource(iconResId)
        }
    }

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: String): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCT_ID, productId)
                }
            }
        }
    }
}