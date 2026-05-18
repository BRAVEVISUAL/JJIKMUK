package com.coworker.jjikmuk.feature.history.chat

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
        val actionWidth = dp(72)
        val iconSize = dp(24)
        val pinBackground = ColorDrawable(Color.parseColor("#FFD66B"))
        val deleteBackground = ColorDrawable(Color.parseColor("#FF6262"))
        val pinIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_swipe_action_left)
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_swipe_action_right)
        var openedPosition = RecyclerView.NO_POSITION
        var openedDirection = 0
        var pendingOpenPosition = RecyclerView.NO_POSITION
        var pendingOpenDirection = 0

        fun closeOpenedItem() {
            if (openedPosition == RecyclerView.NO_POSITION) return

            rvChatHistories.findViewHolderForAdapterPosition(openedPosition)
                ?.itemView
                ?.animate()
                ?.translationX(0f)
                ?.setDuration(120L)
                ?.start()

            openedPosition = RecyclerView.NO_POSITION
            openedDirection = 0
            rvChatHistories.invalidateItemDecorations()
        }

        fun drawAction(
            canvas: Canvas,
            itemView: View,
            direction: Int,
            dragDistance: Int = actionWidth
        ) {
            val iconTop = itemView.top + (itemView.height - iconSize) / 2
            val iconBottom = iconTop + iconSize
            val actionDistance = min(actionWidth, dragDistance)

            if (direction == ItemTouchHelper.RIGHT) {
                pinBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + actionDistance,
                    itemView.bottom
                )
                pinBackground.draw(canvas)

                val iconLeft = itemView.left + (actionWidth - iconSize) / 2
                pinIcon?.setBounds(
                    iconLeft,
                    iconTop,
                    iconLeft + iconSize,
                    iconBottom
                )
                pinIcon?.draw(canvas)
            } else if (direction == ItemTouchHelper.LEFT) {
                deleteBackground.setBounds(
                    itemView.right - actionDistance,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteBackground.draw(canvas)

                val iconLeft = itemView.right - (actionWidth + iconSize) / 2
                deleteIcon?.setBounds(
                    iconLeft,
                    iconTop,
                    iconLeft + iconSize,
                    iconBottom
                )
                deleteIcon?.draw(canvas)
            }
        }

        rvChatHistories.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                if (openedPosition == RecyclerView.NO_POSITION || openedDirection == 0) return

                val viewHolder = parent.findViewHolderForAdapterPosition(openedPosition) ?: return
                drawAction(c, viewHolder.itemView, openedDirection)
            }
        })

        rvChatHistories.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
                if (openedPosition == RecyclerView.NO_POSITION) return false
                if (event.action != MotionEvent.ACTION_UP) return false

                val viewHolder = rv.findViewHolderForAdapterPosition(openedPosition) ?: run {
                    closeOpenedItem()
                    return false
                }

                val itemView = viewHolder.itemView
                val actionRect = if (openedDirection == ItemTouchHelper.RIGHT) {
                    Rect(itemView.left, itemView.top, itemView.left + actionWidth, itemView.bottom)
                } else {
                    Rect(itemView.right - actionWidth, itemView.top, itemView.right, itemView.bottom)
                }

                val tappedAction = actionRect.contains(event.x.toInt(), event.y.toInt())
                val history = chatHistoryAdapter.currentList.getOrNull(openedPosition)

                if (tappedAction && history != null) {
                    when (openedDirection) {
                        ItemTouchHelper.RIGHT -> viewModel.pinChatHistory(history.id)
                        ItemTouchHelper.LEFT -> viewModel.deleteChatHistory(history.id)
                    }
                    openedPosition = RecyclerView.NO_POSITION
                    openedDirection = 0
                    return true
                }

                closeOpenedItem()
                return false
            }
        })

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Float.MAX_VALUE
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Float.MAX_VALUE
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return

                if (pendingOpenPosition == position && pendingOpenDirection != 0) {
                    if (openedPosition != RecyclerView.NO_POSITION && openedPosition != position) {
                        recyclerView.findViewHolderForAdapterPosition(openedPosition)
                            ?.itemView
                            ?.translationX = 0f
                    }

                    openedPosition = position
                    openedDirection = pendingOpenDirection
                    viewHolder.itemView.translationX = if (openedDirection == ItemTouchHelper.RIGHT) {
                        actionWidth.toFloat()
                    } else {
                        -actionWidth.toFloat()
                    }
                    recyclerView.invalidateItemDecorations()
                } else {
                    if (openedPosition == position) {
                        openedPosition = RecyclerView.NO_POSITION
                        openedDirection = 0
                    }
                    viewHolder.itemView.translationX = 0f
                    recyclerView.invalidateItemDecorations()
                }

                pendingOpenPosition = RecyclerView.NO_POSITION
                pendingOpenDirection = 0
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
                val position = viewHolder.bindingAdapterPosition
                val clampedDx = max(-actionWidth.toFloat(), min(actionWidth.toFloat(), dX))
                val direction = when {
                    clampedDx > 0f -> ItemTouchHelper.RIGHT
                    clampedDx < 0f -> ItemTouchHelper.LEFT
                    else -> 0
                }

                if (isCurrentlyActive && position != RecyclerView.NO_POSITION) {
                    pendingOpenPosition = if (abs(clampedDx) >= actionWidth / 2f) {
                        position
                    } else {
                        RecyclerView.NO_POSITION
                    }
                    pendingOpenDirection = if (pendingOpenPosition == RecyclerView.NO_POSITION) {
                        0
                    } else {
                        direction
                    }
                }

                if (direction != 0) {
                    drawAction(c, itemView, direction, abs(clampedDx).toInt())
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    clampedDx,
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
