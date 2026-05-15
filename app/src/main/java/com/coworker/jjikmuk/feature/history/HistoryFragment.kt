package com.coworker.jjikmuk.feature.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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

    private lateinit var favoriteAdapter: RecommendProductAdapter
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

        val btnHistoryBack = view.findViewById<ImageButton>(R.id.btnHistoryBack)
        rvFavoriteProducts = view.findViewById(R.id.rvFavoriteProducts)
        tvEmptyFavoriteProducts = view.findViewById(R.id.tvEmptyFavoriteProducts)

        btnHistoryBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        favoriteAdapter = RecommendProductAdapter { product ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ProductDetailFragment.newInstance(product.id))
                .addToBackStack(null)
                .commit()
        }

        rvFavoriteProducts.layoutManager = LinearLayoutManager(requireContext())
        rvFavoriteProducts.adapter = favoriteAdapter
        rvFavoriteProducts.isNestedScrollingEnabled = false

        BottomNavController.bind(view, parentFragmentManager, requireContext())

        loadFavoriteProducts()
    }

    private fun loadFavoriteProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val favoriteProducts = FavoriteProductStore.getFavoriteProducts(requireContext())

            favoriteAdapter.submitList(favoriteProducts)
            tvEmptyFavoriteProducts.visibility = if (favoriteProducts.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}