package com.coworker.jjikmuk.feature.chat

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.domain.model.ChatMessage
import com.coworker.jjikmuk.feature.product.adapter.RecommendProductAdapter
import com.coworker.jjikmuk.feature.product.detail.ProductDetailFragment
import com.coworker.jjikmuk.feature.product.search.ProductSearchFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()

    private lateinit var scrollChatMessages: ScrollView
    private lateinit var layoutChatMessages: LinearLayout
    private lateinit var etChatMessage: EditText
    private lateinit var btnChatSend: ImageButton
    private lateinit var tvChatTitle: TextView
    private var lastRenderedMessageCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollChatMessages = view.findViewById(R.id.scrollChatMessages)
        layoutChatMessages = view.findViewById(R.id.layoutChatMessages)
        etChatMessage = view.findViewById(R.id.etChatMessage)
        btnChatSend = view.findViewById(R.id.btnChatSend)

        val btnChatBack = view.findViewById<ImageButton>(R.id.btnChatBack)
        tvChatTitle = view.findViewById(R.id.tvChatTitle)

        btnChatBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        observeViewModel()

        val initialMessage = arguments?.getString(ARG_INITIAL_MESSAGE).orEmpty()

        if (initialMessage.isNotBlank()) {
            viewModel.startChat(initialMessage)
        }

        btnChatSend.setOnClickListener {
            sendCurrentMessage()
        }

        etChatMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCurrentMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendCurrentMessage() {
        val message = etChatMessage.text.toString().trim()

        if (message.isEmpty()) return

        etChatMessage.text.clear()
        viewModel.sendMessage(message)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    tvChatTitle.text = state.title
                    renderMessages(state.messages)

                    if (state.shouldShowRecommendSheet) {
                        scrollChatMessages.postDelayed({
                            showRecommendProductBottomSheet()
                            viewModel.onRecommendSheetShown()
                        }, 500)
                    }
                }
            }
        }
    }

    private fun renderMessages(messages: List<ChatMessage>) {
        if (messages.size < lastRenderedMessageCount) {
            layoutChatMessages.removeAllViews()
            lastRenderedMessageCount = 0
        }

        messages.drop(lastRenderedMessageCount).forEach { message ->
            when (message.senderType) {
                ChatMessage.SenderType.USER -> addUserMessage(message.text)
                ChatMessage.SenderType.BOT -> addBotMessage(message.text)
            }
        }

        lastRenderedMessageCount = messages.size
    }

    private fun addUserMessage(message: String) {
        val row = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
            gravity = Gravity.END
            orientation = LinearLayout.HORIZONTAL
        }

        val bubble = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.68f).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = requireContext().getDrawable(R.drawable.bg_chat_user)
            gravity = Gravity.CENTER
            minHeight = dp(44)
            setPadding(dp(18), dp(10), dp(18), dp(10))
            text = message
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
        }

        row.addView(bubble)
        layoutChatMessages.addView(row)
        scrollToBottom()
    }

    private fun addBotMessage(message: String) {
        val row = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(20)
            }
            gravity = Gravity.START
            orientation = LinearLayout.HORIZONTAL
        }

        val dot = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(dp(18), dp(18)).apply {
                rightMargin = dp(8)
                topMargin = dp(26)
            }
            background = requireContext().getDrawable(R.drawable.bg_camera_circle)
            alpha = 0.35f
        }

        val bubble = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.62f).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = requireContext().getDrawable(R.drawable.bg_chat_bot)
            minHeight = dp(44)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            text = message
            setTextColor(0xFF555555.toInt())
            textSize = 12f
        }

        row.addView(dot)
        row.addView(bubble)
        layoutChatMessages.addView(row)
        scrollToBottom()
    }

    private fun showRecommendProductBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val bottomSheetView = layoutInflater.inflate(
            R.layout.bottom_sheet_recommend_products,
            null
        )

        val rvRecommendProducts =
            bottomSheetView.findViewById<RecyclerView>(R.id.rvRecommendProducts)

        val btnMoreProducts =
            bottomSheetView.findViewById<TextView>(R.id.btnMoreProducts)

        val adapter = RecommendProductAdapter { product ->
            dialog.dismiss()

            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ProductDetailFragment.newInstance(product.id))
                .addToBackStack(null)
                .commit()
        }

        rvRecommendProducts.layoutManager = LinearLayoutManager(requireContext())
        rvRecommendProducts.adapter = adapter
        rvRecommendProducts.isNestedScrollingEnabled = false

        adapter.submitList(viewModel.uiState.value.recommendedProducts)

        btnMoreProducts.setOnClickListener {
            dialog.dismiss()

            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ProductSearchFragment())
                .addToBackStack(null)
                .commit()
        }

        dialog.setContentView(bottomSheetView)
        dialog.show()
    }

    private fun scrollToBottom() {
        scrollChatMessages.post {
            scrollChatMessages.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    companion object {
        private const val ARG_INITIAL_MESSAGE = "initial_message"

        fun newInstance(initialMessage: String): ChatFragment {
            return ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_MESSAGE, initialMessage)
                }
            }
        }
    }
}