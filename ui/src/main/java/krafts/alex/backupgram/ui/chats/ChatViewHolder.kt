package krafts.alex.backupgram.ui.chats

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*

class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    val message: TextView = view.message
    val avatar: ImageView = view.avatar
    val edit: AppCompatImageView = view.edited
    val remove: AppCompatImageView = view.removed

    override fun toString(): String {
        return super.toString() + " '" + message.text + "'"
    }
}