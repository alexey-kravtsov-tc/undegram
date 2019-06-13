package krafts.alex.backupgram.ui.chat

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*

class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    val message: TextView = view.message
    val date: TextView = view.date
    val avatar: ImageView = view.avatar
    val editList: RecyclerView = view.editList

    override fun toString(): String {
        return super.toString() + " '" + message.text + "'"
    }
}