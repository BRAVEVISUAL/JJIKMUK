package com.coworker.jjikmuk.feature.history

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.feature.history.product.ProductHistoryAdapter
import com.coworker.jjikmuk.feature.navigation.BottomNavController
import com.coworker.jjikmuk.feature.product.detail.ProductDetailFragment
import com.coworker.jjikmuk.feature.product.mapper.toUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var favoriteAdapter: ProductHistoryAdapter
    private lateinit var rvFavoriteProducts: RecyclerView
    private lateinit var tvEmptyFavoriteProducts: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupClickListeners(view)
        setupHistoryTabClickListeners(view)
        observeViewModel()

        viewModel.loadFavoriteProducts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoriteProducts()
    }

    private fun initViews(view: View) {
        val btnHistoryBack = view.findViewById<ImageButton>(R.id.btnHistoryBack)

        rvFavoriteProducts = view.findViewById(R.id.rvFavoriteProducts)
        tvEmptyFavoriteProducts = view.findViewById(R.id.tvEmptyFavoriteProducts)

        btnHistoryBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        favoriteAdapter = ProductHistoryAdapter(
            onProductClick = { product ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, ProductDetailFragment.newInstance(product.id))
                    .addToBackStack(null)
                    .commit()
            },
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product.id)
            }
        )

        rvFavoriteProducts.layoutManager = LinearLayoutManager(requireContext())
        rvFavoriteProducts.adapter = favoriteAdapter
        rvFavoriteProducts.isNestedScrollingEnabled = false
    }

    private fun setupClickListeners(view: View) {
        BottomNavController.bind(view, parentFragmentManager, requireContext())
    }

    private fun setupHistoryTabClickListeners(view: View) {
        val layoutLikesTab = view.findViewById<LinearLayout>(R.id.layoutLikesTab)
        val layoutRecentlyViewedTab = view.findViewById<LinearLayout>(R.id.layoutRecentlyViewedTab)

        layoutLikesTab.setOnClickListener {
            viewModel.selectLikesTab()
        }

        layoutRecentlyViewedTab.setOnClickListener {
            viewModel.selectRecentlyViewedTab()
        }
    }

    private fun updateHistoryTabStyle(
        view: View,
        selectedTab: HistoryTab
    ) {
        val layoutLikesTab = view.findViewById<LinearLayout>(R.id.layoutLikesTab)
        val layoutRecentlyViewedTab = view.findViewById<LinearLayout>(R.id.layoutRecentlyViewedTab)
        val tvLikesTab = view.findViewById<TextView>(R.id.tvLikesTab)
        val tvRecentlyViewedTab = view.findViewById<TextView>(R.id.tvRecentlyViewedTab)
        val ivLikesIcon = view.findViewById<ImageView>(R.id.ivLikesIcon)
        val tvRecentlyViewedIcon = view.findViewById<TextView>(R.id.tvRecentlyViewedIcon)

        val isLikesSelected = selectedTab == HistoryTab.LIKES
        val selectedGreen = Color.parseColor("#E8FCD4")
        val unselectedGray = Color.parseColor("#A0A0A5")
        val selectedText = Color.parseColor("#111111")
        val unselectedText = Color.WHITE

        layoutLikesTab.backgroundTintList = ColorStateList.valueOf(
            if (isLikesSelected) selectedGreen else unselectedGray
        )
        layoutRecentlyViewedTab.backgroundTintList = ColorStateList.valueOf(
            if (isLikesSelected) unselectedGray else selectedGreen
        )

        tvLikesTab.setTextColor(if (isLikesSelected) selectedText else unselectedText)
        tvRecentlyViewedTab.setTextColor(if (isLikesSelected) unselectedText else selectedText)
        tvRecentlyViewedIcon.setTextColor(if (isLikesSelected) unselectedText else selectedText)

        ivLikesIcon.imageTintList = ColorStateList.valueOf(
            if (isLikesSelected) selectedText else unselectedText
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val favoriteProductUiModels = state.favoriteProducts.map { product ->
                        product.toUiModel().copy(
                            isFavorite = !state.unfavoritedProductIds.contains(product.id)
                        )
                    }

                    favoriteAdapter.submitList(favoriteProductUiModels)

                    tvEmptyFavoriteProducts.visibility = if (state.favoriteProducts.isEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    updateHistoryTabStyle(
                        view = requireView(),
                        selectedTab = state.selectedTab
                    )
                }
            }
        }
    }
}