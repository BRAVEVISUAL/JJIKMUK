package com.coworker.jjikmuk.feature.history.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.core.navigation.BottomNavController
import com.coworker.jjikmuk.feature.history.chat.adapter.ChatHistoryAdapter
import kotlinx.coroutines.launch

class ChatHistoryFragment : Fragment(R.layout.fragment_chat_history) {

    private val viewModel: ChatHistoryViewModel by viewModels()

    private lateinit var chatHistoryAdapter: ChatHistoryAdapter
    private lateinit var etChatHistorySearch: EditText
    private lateinit var tvEmptyChatHistories: TextView
    private lateinit var tvChatHistoryEdit: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView(view)
        setupClickListeners(view)
        setupSearchWatcher()
        observeViewModel()
    }

    private fun initViews(view: View) {
        etChatHistorySearch = view.findViewById(R.id.etChatHistorySearch)
        tvEmptyChatHistories = view.findViewById(R.id.tvEmptyChatHistories)
        tvChatHistoryEdit = view.findViewById(R.id.tvChatHistoryEdit)
    }

    private fun setupRecyclerView(view: View) {
        chatHistoryAdapter = ChatHistoryAdapter { history ->
            Toast.makeText(requireContext(), history.title, Toast.LENGTH_SHORT).show()
        }

        val rvChatHistories = view.findViewById<RecyclerView>(R.id.rvChatHistories)
        rvChatHistories.layoutManager = LinearLayoutManager(requireContext())
        rvChatHistories.adapter = chatHistoryAdapter
        rvChatHistories.isNestedScrollingEnabled = false
    }

    private fun setupClickListeners(view: View) {
        val btnChatHistoryBack = view.findViewById<ImageButton>(R.id.btnChatHistoryBack)

        btnChatHistoryBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvChatHistoryEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }

        BottomNavController.bind(view, parentFragmentManager, requireContext())
    }

    private fun setupSearchWatcher() {
        etChatHistorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updateSearchQuery(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    chatHistoryAdapter.submitList(state.filteredHistories)
                    tvEmptyChatHistories.visibility = if (state.filteredHistories.isEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    tvChatHistoryEdit.text = if (state.isEditMode) "완료" else "편집"
                }
            }
        }
    }
}
