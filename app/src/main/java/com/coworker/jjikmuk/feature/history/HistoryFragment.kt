package com.coworker.jjikmuk.feature.history

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.core.navigation.BottomNavController
import com.coworker.jjikmuk.data.local.preference.FavoriteProductStore
import com.coworker.jjikmuk.feature.chat.adapter.RecommendProductAdapter
import com.coworker.jjikmuk.feature.product.detail.ProductDetailFragment
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()

        val root = ConstraintLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
            clipChildren = false
            clipToPadding = false
        }

        val bottomNav = inflater.inflate(R.layout.layout_bottom_nav, root, false).apply {
            id = R.id.layoutBottomNav
        }

        val scrollView = ScrollView(context).apply {
            id = View.generateViewId()
            overScrollMode = View.OVER_SCROLL_NEVER
            clipToPadding = false
        }

        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(40), dp(20), dp(24))
        }

        val topRow = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
            )
        }

        val backButton = ImageButton(context).apply {
            setImageResource(R.drawable.ic_back)
            background = null
            contentDescription = "뒤로가기"
            setPadding(0, 0, 0, 0)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        val profileDot = View(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.bg_profile_circle)
        }

        topRow.addView(
            backButton,
            FrameLayout.LayoutParams(dp(24), dp(24)).apply {
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
        )

        topRow.addView(
            profileDot,
            FrameLayout.LayoutParams(dp(24), dp(24)).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            }
        )

        val title = TextView(context).apply {
            text = "History"
            setTextColor(ContextCompat.getColor(context, R.color.jjikmuk_green))
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
        }

        val tabRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
            ).apply {
                topMargin = dp(16)
            }
        }

        val likesTab = TextView(context).apply {
            text = "Likes  ♡"
            textSize = 13f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(context, R.drawable.bg_camera_circle)
        }

        val recentTab = TextView(context).apply {
            text = "Recently Viewed  ◷"
            textSize = 13f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(context, R.drawable.bg_input_box)
        }

        tabRow.addView(
            likesTab,
            LinearLayout.LayoutParams(dp(84), dp(36))
        )

        tabRow.addView(
            recentTab,
            LinearLayout.LayoutParams(dp(154), dp(36)).apply {
                marginStart = dp(8)
            }
        )

        val favoriteAdapter = RecommendProductAdapter { product ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ProductDetailFragment.newInstance(product.id))
                .addToBackStack(null)
                .commit()
        }

        val recyclerView = RecyclerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = favoriteAdapter
        }

        val emptyText = TextView(context).apply {
            text = "좋아요한 상품이 없습니다."
            textSize = 14f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(160)
            ).apply {
                topMargin = dp(16)
            }
        }

        contentLayout.addView(topRow)
        contentLayout.addView(title)
        contentLayout.addView(tabRow)
        contentLayout.addView(recyclerView)
        contentLayout.addView(emptyText)

        lifecycleScope.launch {
            val favoriteProducts = FavoriteProductStore.getFavoriteProducts(requireContext())
            favoriteAdapter.submitList(favoriteProducts)
            emptyText.visibility = if (favoriteProducts.isEmpty()) View.VISIBLE else View.GONE
        }

        scrollView.addView(contentLayout)
        root.addView(scrollView)
        root.addView(bottomNav)

        scrollView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToTop = R.id.layoutBottomNav
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }

        bottomNav.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            dp(92)
        ).apply {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }

        root.post {
            BottomNavController.bind(root, parentFragmentManager, requireContext())
        }

        return root
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}