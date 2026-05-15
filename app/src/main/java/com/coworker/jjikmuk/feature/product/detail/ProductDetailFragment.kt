package com.coworker.jjikmuk.feature.product.detail

import android.content.Context
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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.feature.chat.adapter.RecommendProductAdapter
import com.coworker.jjikmuk.feature.chat.model.RecommendProduct
import com.coworker.jjikmuk.feature.home.HomeFragment
import com.coworker.jjikmuk.feature.product.dummy.ProductDummyData

object FavoriteProductStore {
    private const val PREF_NAME = "favorite_product_preferences"
    private const val KEY_FAVORITE_PRODUCT_IDS = "favorite_product_ids"
    private const val SEPARATOR = ","

    fun isFavorite(context: Context, productId: String): Boolean {
        return getFavoriteProductIds(context).contains(productId)
    }

    fun toggle(context: Context, productId: String): Boolean {
        val favoriteIds = getFavoriteProductIds(context).toMutableSet()

        val isNowFavorite = if (favoriteIds.contains(productId)) {
            favoriteIds.remove(productId)
            false
        } else {
            favoriteIds.add(productId)
            true
        }

        saveFavoriteProductIds(context, favoriteIds)
        return isNowFavorite
    }

    fun getFavoriteProducts(context: Context): List<RecommendProduct> {
        val favoriteIds = getFavoriteProductIds(context)

        return ProductDummyData.recommendProducts.filter { product ->
            favoriteIds.contains(product.id)
        }
    }

    private fun getFavoriteProductIds(context: Context): Set<String> {
        val savedValue = context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FAVORITE_PRODUCT_IDS, "")
            .orEmpty()

        if (savedValue.isBlank()) return emptySet()

        return savedValue
            .split(SEPARATOR)
            .filter { productId -> productId.isNotBlank() }
            .toSet()
    }

    private fun saveFavoriteProductIds(context: Context, favoriteIds: Set<String>) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FAVORITE_PRODUCT_IDS, favoriteIds.joinToString(SEPARATOR))
            .apply()
    }
}

object BottomNavController {

    fun bind(
        rootView: View,
        fragmentManager: FragmentManager,
        context: Context
    ) {
        rootView.findViewById<View?>(R.id.navHome)?.setOnClickListener {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            fragmentManager.beginTransaction()
                .replace(R.id.mainContainer, HomeFragment())
                .commit()
        }

        rootView.findViewById<View?>(R.id.navDiet)?.setOnClickListener {
            showComingSoon(context)
        }

        rootView.findViewById<View?>(R.id.navCamera)?.setOnClickListener {
            showComingSoon(context)
        }

        rootView.findViewById<View?>(R.id.navHistory)?.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.mainContainer, HistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        rootView.findViewById<View?>(R.id.navMy)?.setOnClickListener {
            showComingSoon(context)
        }
    }

    private fun showComingSoon(context: Context) {
        Toast.makeText(context, "향후 구현 예정입니다", Toast.LENGTH_SHORT).show()
    }
}

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
            FavoriteProductStore.toggle(requireContext(), currentProductId)
            updateFavoriteIcon()
        }
    }

    private fun updateFavoriteIcon() {
        val iconResId = if (FavoriteProductStore.isFavorite(requireContext(), currentProductId)) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_outline
        }

        btnProductFavorite?.setImageResource(iconResId)
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

        val favoriteProducts = FavoriteProductStore.getFavoriteProducts(context)

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

        favoriteAdapter.submitList(favoriteProducts)

        val emptyText = TextView(context).apply {
            text = "좋아요한 상품이 없습니다."
            textSize = 14f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            visibility = if (favoriteProducts.isEmpty()) View.VISIBLE else View.GONE
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

        scrollView.addView(contentLayout)
        root.addView(scrollView)
        root.addView(bottomNav)

        val scrollParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToTop = R.id.layoutBottomNav
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        scrollView.layoutParams = scrollParams

        val bottomNavParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            dp(92)
        ).apply {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        bottomNav.layoutParams = bottomNavParams

        root.post {
            BottomNavController.bind(root, parentFragmentManager, requireContext())
        }

        return root
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}