package com.coworker.jjikmuk.feature.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.feature.product.adapter.RecommendProductAdapter
import com.coworker.jjikmuk.feature.product.model.ProductUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class RecommendProductBottomSheet(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val title: String = "원하시는 상품을 선택해주세요",
    private val products: List<ProductUiModel>,
    private val bottomOffsetPx: Int = 0,
    private val showMoreButton: Boolean = true,
    private val onProductClick: (ProductUiModel) -> Unit,
    private val onMoreClick: (() -> Unit)? = null
) {
    fun show() {
        val dialog = BottomSheetDialog(context)
        dialog.setCanceledOnTouchOutside(false)

        val bottomSheetView = layoutInflater.inflate(
            R.layout.bottom_sheet_recommend_products,
            null
        )

        val rvRecommendProducts =
            bottomSheetView.findViewById<RecyclerView>(R.id.rvRecommendProducts)

        val tvRecommendSheetTitle =
            bottomSheetView.findViewById<TextView>(R.id.tvRecommendSheetTitle)

        val btnMoreProducts =
            bottomSheetView.findViewById<TextView>(R.id.btnMoreProducts)

        tvRecommendSheetTitle.text = title

        val adapter = RecommendProductAdapter { product ->
            dialog.dismiss()
            onProductClick(product)
        }

        rvRecommendProducts.layoutManager = LinearLayoutManager(bottomSheetView.context)
        rvRecommendProducts.adapter = adapter
        rvRecommendProducts.isNestedScrollingEnabled = false
        adapter.submitList(products)

        btnMoreProducts.visibility = if (showMoreButton) View.VISIBLE else View.GONE
        btnMoreProducts.setOnClickListener {
            dialog.dismiss()
            onMoreClick?.invoke()
        }

        dialog.setContentView(bottomSheetView)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            ) ?: return@setOnShowListener

            val layoutParams = bottomSheet.layoutParams as? CoordinatorLayout.LayoutParams
            if (layoutParams != null) {
                layoutParams.bottomMargin = bottomOffsetPx
                bottomSheet.layoutParams = layoutParams
            }

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = context.dpToPx(COLLAPSED_HANDLE_HEIGHT_DP)
            behavior.isHideable = false
            behavior.skipCollapsed = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            dialog.findViewById<View>(
                com.google.android.material.R.id.touch_outside
            )?.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        dialog.show()
    }

    private fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    companion object {
        private const val COLLAPSED_HANDLE_HEIGHT_DP = 32
    }
}
