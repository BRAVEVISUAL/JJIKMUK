package com.coworker.jjikmuk.feature.product.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.feature.chat.adapter.RecommendProductAdapter
import com.coworker.jjikmuk.feature.product.dummy.ProductDummyData
import com.coworker.jjikmuk.feature.product.detail.ProductDetailFragment
import com.coworker.jjikmuk.feature.product.detail.BottomNavController

class ProductSearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_product_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnProductSearchBack = view.findViewById<View>(R.id.btnProductSearchBack)
        val rvProductSearchResults =
            view.findViewById<RecyclerView>(R.id.rvProductSearchResults)

        btnProductSearchBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val adapter = RecommendProductAdapter { product ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ProductDetailFragment.newInstance(product.id)).addToBackStack(null)
                .commit()
        }

        rvProductSearchResults.layoutManager = LinearLayoutManager(requireContext())
        rvProductSearchResults.adapter = adapter
        adapter.submitList(ProductDummyData.recommendProducts)

        BottomNavController.bind(view, parentFragmentManager, requireContext())
    }
}