package krafts.alex.backupgram.ui.chats

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.messages.MessageViewHolder
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.Message
import java.io.File

class ChatAdapter(
    private var values: List<Message>
) : RecyclerView.Adapter<MessageViewHolder>() {

    fun setAll(items: List<Message>) {
        values = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = values[position]

        holder.name.text = item.user?.let { it.firstName + " " + it.lastName }

        item.user?.photoBig?.let {
            if (it.downloaded)
                Picasso
                    .get()
                    .load(File(it.localPath))
                    .transform(CircleTransform())
                    .into(holder.avatar)
        }

        holder.message.text = item.text

        if (position != 0 && item.senderId == values[position - 1].senderId) {
            holder.name.visibility = View.GONE
            holder.avatar.setColorFilter(Color.WHITE)
        }
        with(holder.view) {
            tag = item
        }
    }

    override fun getItemCount(): Int = values.size

}