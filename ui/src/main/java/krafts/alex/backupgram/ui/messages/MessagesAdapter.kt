package krafts.alex.backupgram.ui.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.Message
import java.io.File

class MessagesAdapter(
    private var values: List<Message>
) : RecyclerView.Adapter<MessageViewHolder>() {

    fun setAll(items: List<Message>) {
        values = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = values[position]


        if (item.chat == null) {
            BackApp.client.getChatInfo(item.chatId)
        }

        holder.name.text = item?.chat?.title ?:
            item.user?.let { it.firstName + " " + it.lastName }

        (item?.chat?.photoBig ?: item.user?.photoBig)?.let {
            if (it.downloaded)
                Picasso
                    .get()
                    .load(File(it.localPath))
                    .transform(CircleTransform())
                    .into(holder.avatar)
        }

        holder.message.text = item.text

        with(holder.view) {
            tag = item
            setOnClickListener { v ->
                val item = v.tag as Message
                val action =
                    MessagesFragmentDirections.actionChatDetails(
                        item.chatId
                    )
                Navigation.findNavController(v).navigate(action)
            }
        }
    }

    override fun getItemCount(): Int = values.size
}

