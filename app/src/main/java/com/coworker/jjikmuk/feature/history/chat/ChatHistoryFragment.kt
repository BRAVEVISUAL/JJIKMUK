package com.coworker.jjikmuk.feature.history.chat

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
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
        val directionLeft = -1
        val directionRight = 1
        val actionWidth = dp(80)
        val openThreshold = actionWidth * 0.95f
        val touchSlop = ViewConfiguration.get(requireContext()).scaledTouchSlop
        val autoCloseHandler = Handler(Looper.getMainLooper())
        val pinIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_swipe_action_left)
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_swipe_action_right)

        var openedPosition = RecyclerView.NO_POSITION
        var openedDirection = 0
        var activePosition = RecyclerView.NO_POSITION
        var activeView: View? = null
        var downX = 0f
        var downY = 0f
        var startTranslationX = 0f
        var activeDistance = 0
        var activeDirection = 0
        var isDragging = false

        fun drawAction(
            canvas: Canvas,
            itemView: View,
            direction: Int,
            dragDistance: Int = actionWidth
        ) {
            val actionDistance = min(actionWidth, dragDistance)
            if (actionDistance <= 0) return

            if (direction == directionRight) {
                val actionLeft = itemView.left
                val actionRight = itemView.left + actionDistance

                canvas.save()
                canvas.clipRect(actionLeft, itemView.top, actionRight, itemView.bottom)
                pinIcon?.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + actionWidth,
                    itemView.bottom
                )
                pinIcon?.draw(canvas)
                canvas.restore()
            } else if (direction == directionLeft) {
                val actionLeft = itemView.right - actionDistance
                val actionRight = itemView.right

                canvas.save()
                canvas.clipRect(actionLeft, itemView.top, actionRight, itemView.bottom)
                deleteIcon?.setBounds(
                    itemView.right - actionWidth,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteIcon?.draw(canvas)
                canvas.restore()
            }
        }

        fun closeOpenedItem(animate: Boolean = true) {
            autoCloseHandler.removeCallbacksAndMessages(null)

            if (openedPosition == RecyclerView.NO_POSITION) return

            val openedView = rvChatHistories.findViewHolderForAdapterPosition(openedPosition)?.itemView
            if (animate) {
                openedView?.animate()
                    ?.translationX(0f)
                    ?.setDuration(120L)
                    ?.start()
            } else {
                openedView?.translationX = 0f
            }

            openedPosition = RecyclerView.NO_POSITION
            openedDirection = 0
            rvChatHistories.invalidateItemDecorations()
        }

        fun scheduleAutoClose() {
            autoCloseHandler.removeCallbacksAndMessages(null)
            autoCloseHandler.postDelayed({
                closeOpenedItem()
            }, 2_000L)
        }

        fun actionRect(itemView: View, direction: Int): Rect {
            return if (direction == directionRight) {
                Rect(itemView.left, itemView.top, itemView.left + actionWidth, itemView.bottom)
            } else {
                Rect(itemView.right - actionWidth, itemView.top, itemView.right, itemView.bottom)
            }
        }

        fun clearActiveDrag() {
            activePosition = RecyclerView.NO_POSITION
            activeView = null
            activeDistance = 0
            activeDirection = 0
            isDragging = false
        }

        rvChatHistories.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                if (openedPosition != RecyclerView.NO_POSITION && openedDirection != 0) {
                    parent.findViewHolderForAdapterPosition(openedPosition)?.itemView?.let { itemView ->
                        drawAction(c, itemView, openedDirection)
                    }
                }

                if (isDragging && activeView != null && activeDirection != 0) {
                    drawAction(c, activeView ?: return, activeDirection, activeDistance)
                }
            }
        })

        rvChatHistories.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        autoCloseHandler.removeCallbacksAndMessages(null)
                        downX = event.x
                        downY = event.y
                        isDragging = false
                        activeDistance = 0
                        activeDirection = 0

                        if (openedPosition != RecyclerView.NO_POSITION) {
                            val openedHolder = rv.findViewHolderForAdapterPosition(openedPosition)
                            val openedView = openedHolder?.itemView
                            val history = chatHistoryAdapter.currentList.getOrNull(openedPosition)

                            if (
                                openedView != null &&
                                history != null &&
                                actionRect(openedView, openedDirection).contains(event.x.toInt(), event.y.toInt())
                            ) {
                                when (openedDirection) {
                                    directionRight -> viewModel.pinChatHistory(history.id)
                                    directionLeft -> viewModel.deleteChatHistory(history.id)
                                }
                                openedPosition = RecyclerView.NO_POSITION
                                openedDirection = 0
                                rv.invalidateItemDecorations()
                                return true
                            }

                            closeOpenedItem()
                            return true
                        }

                        val child = rv.findChildViewUnder(event.x, event.y) ?: return false
                        activeView = child
                        activePosition = rv.getChildAdapterPosition(child)
                        startTranslationX = child.translationX
                        return false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val targetView = activeView ?: return false
                        if (activePosition == RecyclerView.NO_POSITION) return false

                        val dx = event.x - downX
                        val dy = event.y - downY
                        if (abs(dx) <= touchSlop || abs(dx) <= abs(dy)) return false

                        isDragging = true
                        rv.parent?.requestDisallowInterceptTouchEvent(true)
                        targetView.animate().cancel()
                        return true
                    }
                }

                return false
            }

            override fun onTouchEvent(rv: RecyclerView, event: MotionEvent) {
                val targetView = activeView ?: return
                if (activePosition == RecyclerView.NO_POSITION) return

                when (event.actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.x - downX + startTranslationX
                        val clampedDx = max(-actionWidth.toFloat(), min(actionWidth.toFloat(), dx))
                        targetView.translationX = clampedDx

                        activeDirection = when {
                            clampedDx > 0f -> directionRight
                            clampedDx < 0f -> directionLeft
                            else -> 0
                        }
                        activeDistance = abs(clampedDx).toInt()
                        rv.invalidateItemDecorations()
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        val currentDirection = activeDirection
                        val currentDistance = activeDistance
                        val currentPosition = activePosition

                        if (currentDirection != 0 && currentDistance >= openThreshold) {
                            openedPosition = currentPosition
                            openedDirection = currentDirection
                            targetView.translationX = if (currentDirection == directionRight) {
                                actionWidth.toFloat()
                            } else {
                                -actionWidth.toFloat()
                            }
                            rv.invalidateItemDecorations()
                            scheduleAutoClose()
                        } else {
                            targetView.animate()
                                .translationX(0f)
                                .setDuration(120L)
                                .start()
                            if (openedPosition == currentPosition) {
                                openedPosition = RecyclerView.NO_POSITION
                                openedDirection = 0
                            }
                            rv.invalidateItemDecorations()
                        }

                        clearActiveDrag()
                    }
                }
            }
        })
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
