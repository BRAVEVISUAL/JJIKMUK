package com.coworker.jjikmuk.feature.history.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.domain.model.ChatHistory

class ChatHistoryAdapter(
    private val onItemClick: (ChatHistory) -> Unit
) : ListAdapter<ChatHistory, ChatHistoryAdapter.ChatHistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat_history,
            parent,
            false
        )
        return ChatHistoryViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatHistoryViewHolder(
        itemView: View,
        private val onItemClick: (ChatHistory) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvChatHistoryTitle: TextView = itemView.findViewById(R.id.tvChatHistoryTitle)
        private val tvChatHistorySubtitle: TextView = itemView.findViewById(R.id.tvChatHistorySubtitle)

        fun bind(history: ChatHistory) {
            tvChatHistoryTitle.text = history.title
            tvChatHistorySubtitle.text = history.subtitle
            itemView.setOnClickListener {
                onItemClick(history)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ChatHistory>() {
        override fun areItemsTheSame(oldItem: ChatHistory, newItem: ChatHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatHistory, newItem: ChatHistory): Boolean {
            return oldItem == newItem
        }
    }
}
