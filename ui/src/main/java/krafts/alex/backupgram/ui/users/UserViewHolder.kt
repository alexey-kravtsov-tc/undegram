package krafts.alex.backupgram.ui.users

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    val avatar: ImageView = view.avatar
}