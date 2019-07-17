package krafts.alex.backupgram.ui.chatList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import krafts.alex.backupgram.ui.R
import krafts.alex.tg.entity.ChatWithLastMessage

class ChatsAdapter : PagedListAdapter<ChatWithLastMessage, ChatViewHolder>(ChatsDiffCallback()) {

    class ChatsDiffCallback : DiffUtil.ItemCallback<ChatWithLastMessage>() {
        override fun areItemsTheSame(
            oldItem: ChatWithLastMessage, newItem: ChatWithLastMessage
        ): Boolean = oldItem.chatId == newItem.chatId

        override fun areContentsTheSame(
            oldItem: ChatWithLastMessage, newItem: ChatWithLastMessage
        ): Boolean = oldItem.text == newItem.text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) } ?: holder.clear()
    }
}