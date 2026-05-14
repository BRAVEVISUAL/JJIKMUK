package com.coworker.jjikmuk.feature.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.feature.product.dummy.ProductDummyData

class ProductDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getString(ARG_PRODUCT_ID).orEmpty()
        val product = ProductDummyData.findProductById(productId)

        val btnProductDetailBack = view.findViewById<ImageButton>(R.id.btnProductDetailBack)
        val ivProductDetailImage = view.findViewById<ImageView>(R.id.ivProductDetailImage)
        val tvProductDetailCategory = view.findViewById<TextView>(R.id.tvProductDetailCategory)
        val tvProductDetailName = view.findViewById<TextView>(R.id.tvProductDetailName)
        val tvDetailAllergyTag1 = view.findViewById<TextView>(R.id.tvDetailAllergyTag1)
        val tvDetailAllergyTag2 = view.findViewById<TextView>(R.id.tvDetailAllergyTag2)

        btnProductDetailBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (product == null) {
            tvProductDetailCategory.text = "상품 정보 없음"
            tvProductDetailName.text = "상품을 찾을 수 없습니다."
            tvDetailAllergyTag1.visibility = View.GONE
            tvDetailAllergyTag2.visibility = View.GONE
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