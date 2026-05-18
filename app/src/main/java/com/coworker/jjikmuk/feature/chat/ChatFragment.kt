package com.coworker.jjikmuk.feature.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.coworker.jjikmuk.domain.model.UploadOption
import com.coworker.jjikmuk.feature.chat.adapter.ChatMessageAdapter
import com.coworker.jjikmuk.feature.product.adapter.RecommendProductAdapter
import com.coworker.jjikmuk.feature.product.detail.ProductDetailFragment
import com.coworker.jjikmuk.feature.product.search.ProductSearchFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()

    private lateinit var rvChatMessages: RecyclerView
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var etChatMessage: EditText
    private lateinit var btnChatSend: ImageButton
    private lateinit var tvChatTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvChatMessages = view.findViewById(R.id.rvChatMessages)
        etChatMessage = view.findViewById(R.id.etChatMessage)
        btnChatSend = view.findViewById(R.id.btnChatSend)
        val btnChatPlus = view.findViewById<ImageButton>(R.id.btnChatPlus)

        setupChatMessageRecyclerView()

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

        btnChatPlus.setOnClickListener {
            etChatMessage.clearFocus()
            showUploadOptionBottomSheet()
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

    private fun setupChatMessageRecyclerView() {
        chatMessageAdapter = ChatMessageAdapter()

        rvChatMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        rvChatMessages.adapter = chatMessageAdapter
        rvChatMessages.isNestedScrollingEnabled = false
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
                launch {
                    viewModel.uiState.collect { state ->
                        tvChatTitle.text = state.title
                        chatMessageAdapter.submitList(state.messages) {
                            scrollToBottom()
                        }

                        if (state.shouldShowRecommendSheet) {
                            rvChatMessages.postDelayed({
                                showRecommendProductBottomSheet()
                                viewModel.onRecommendSheetShown()
                            }, 500)
                        }
                    }
                }

                launch {
                    viewModel.uploadOptionEvent.collect { option ->
                        handleUploadOption(option)
                    }
                }
            }
        }
    }

    private fun showUploadOptionBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(
            R.layout.bottom_sheet_home_upload_options,
            null
        )

        bottomSheetView.findViewById<View>(R.id.layoutTakePhoto).setOnClickListener {
            dialog.dismiss()
            viewModel.onUploadOptionSelected(UploadOption.TAKE_PHOTO)
        }

        bottomSheetView.findViewById<View>(R.id.layoutUploadImage).setOnClickListener {
            dialog.dismiss()
            viewModel.onUploadOptionSelected(UploadOption.UPLOAD_IMAGE)
        }

        bottomSheetView.findViewById<View>(R.id.layoutUploadFile).setOnClickListener {
            dialog.dismiss()
            viewModel.onUploadOptionSelected(UploadOption.UPLOAD_FILE)
        }

        dialog.setContentView(bottomSheetView)
        dialog.show()
    }

    private fun handleUploadOption(option: UploadOption) {
        when (option) {
            UploadOption.TAKE_PHOTO -> {
                Toast.makeText(requireContext(), "사진찍기 기능을 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }

            UploadOption.UPLOAD_IMAGE -> {
                Toast.makeText(requireContext(), "이미지 업로드 기능을 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }

            UploadOption.UPLOAD_FILE -> {
                Toast.makeText(requireContext(), "파일 업로드 기능을 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }
        }
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
        val lastIndex = chatMessageAdapter.itemCount - 1
        if (lastIndex >= 0) {
            rvChatMessages.scrollToPosition(lastIndex)
        }
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