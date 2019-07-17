package krafts.alex.backupgram.ui.chatList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.ChatWithLastMessage
import java.io.File

class ChatsAdapter : RecyclerView.Adapter<ChatViewHolder>() {

    private var values: MutableList<ChatWithLastMessage> = mutableListOf()

    fun setAll(items: List<ChatWithLastMessage>) {
        values = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = values[position]


        holder.name.text = item.title

        (item.photoBig)?.let {
            if (it.downloaded)
                Picasso
                    .get()
                    .load(File(it.localPath))
                    .placeholder(R.drawable.ic_users)
                    .transform(CircleTransform())
                    .into(holder.avatar)
            else {
                BackApp.client?.loadImage(it.fileId)
            }
        } ?: BackApp.client.getChatInfo(item.chatId)

        holder.message.text = item.text
        holder.avatar.transitionName = "avatar${item.chatId}"

        with(holder.view) {
            tag = item
            setOnClickListener { v ->
                val message = v.tag as ChatWithLastMessage
                val action = ChatListFragmentDirections.actionChatDetails(message.chatId)
                val extras = FragmentNavigator.Extras.Builder()
                extras.addSharedElement(
                    holder.avatar, context.getString(R.string.avatar_transition)
                )
                Navigation.findNavController(v).navigate(action, extras.build())
            }
        }
    }

    override fun getItemCount(): Int = values.size
}