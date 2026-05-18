package com.coworker.jjikmuk.feature.history.chat

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
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

        setupSwipeActions(rvChatHistories)
    }

    private fun setupSwipeActions(rvChatHistories: RecyclerView) {
        val pinBackground = ColorDrawable(Color.parseColor("#FFD66B"))
        val deleteBackground = ColorDrawable(Color.parseColor("#FF6262"))
        val pinIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_swipe_action_left)
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_swipe_action_right)
        val iconMargin = dp(28)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return

                val history = chatHistoryAdapter.currentList.getOrNull(position)
                if (history == null) {
                    chatHistoryAdapter.notifyItemChanged(position)
                    return
                }

                when (direction) {
                    ItemTouchHelper.RIGHT -> viewModel.pinChatHistory(history.id)
                    ItemTouchHelper.LEFT -> viewModel.deleteChatHistory(history.id)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconTop = itemView.top + (itemView.height - dp(24)) / 2
                val iconBottom = iconTop + dp(24)

                if (dX > 0) {
                    pinBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                    pinBackground.draw(c)

                    pinIcon?.setBounds(
                        itemView.left + iconMargin,
                        iconTop,
                        itemView.left + iconMargin + dp(24),
                        iconBottom
                    )
                    pinIcon?.draw(c)
                } else if (dX < 0) {
                    deleteBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteBackground.draw(c)

                    deleteIcon?.setBounds(
                        itemView.right - iconMargin - dp(24),
                        iconTop,
                        itemView.right - iconMargin,
                        iconBottom
                    )
                    deleteIcon?.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvChatHistories)
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

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
