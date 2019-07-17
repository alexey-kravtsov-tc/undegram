package krafts.alex.backupgram.ui.chatList

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_message.view.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.ChatWithLastMessage
import java.io.File

class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    val message: TextView = view.message
    val avatar: ImageView = view.avatar

    fun bind(item: ChatWithLastMessage) {
        name.text = item.title

        (item.photoBig)?.let {
            if (it.downloaded)
                Picasso
                    .get()
                    .load(File(it.localPath))
                    .placeholder(R.drawable.ic_users)
                    .transform(CircleTransform())
                    .into(avatar)
            else {
                BackApp.client?.loadImage(it.fileId)
            }
        } ?: BackApp.client.getChatInfo(item.chatId)

        message.text = item.text
        avatar.transitionName = "avatar${item.chatId}"

        with(view) {
            tag = item
            setOnClickListener { v ->
                val message = v.tag as ChatWithLastMessage
                val action = ChatListFragmentDirections.actionChatDetails(message.chatId)
                val extras = androidx.navigation.fragment.FragmentNavigator.Extras.Builder()
                extras.addSharedElement(
                    avatar, context.getString(R.string.avatar_transition)
                )
                androidx.navigation.Navigation.findNavController(v).navigate(action, extras.build())
            }
        }
    }

    fun clear() {
        name.text = null
        message.text = null
        avatar.setImageResource(R.drawable.ic_users)
    }

    override fun toString(): String {
        return super.toString() + " '" + message.text + "'"
    }
}