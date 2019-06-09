package krafts.alex.backupgram.ui.messages

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*

class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    val message: TextView = view.message
    val avatar: ImageView = view.avatar

    override fun toString(): String {
        return super.toString() + " '" + message.text + "'"
    }
}