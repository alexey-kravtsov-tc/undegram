package krafts.alex.backupgram.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import krafts.alex.backupgram.ui.R
import krafts.alex.tg.entity.MessageFromUserWithEdits

class MessagesAdapter(val fragment: Fragment) :
    PagedListAdapter<MessageFromUserWithEdits, MessageViewHolder>(MessagesDiffCallback()) {

    class MessagesDiffCallback : DiffUtil.ItemCallback<MessageFromUserWithEdits>() {
        override fun areItemsTheSame(
            oldItem: MessageFromUserWithEdits, newItem: MessageFromUserWithEdits
        ): Boolean = oldItem.date == newItem.date

        override fun areContentsTheSame(
            oldItem: MessageFromUserWithEdits, newItem: MessageFromUserWithEdits
        ): Boolean = oldItem.text == newItem.text
    }

    fun removeAt(position: Int, block: MessageFromUserWithEdits.() -> Unit = {}) {
        getItem(position)?.let(block)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position == 0 || it.senderId != getItem(position - 1)?.senderId)
        } ?: holder.clear()
    }
}