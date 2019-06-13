package krafts.alex.backupgram.ui.chat.edits

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_edit.view.*

class EditViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val text: TextView = view.edit
    val date: TextView = view.date

    override fun toString(): String {
        return super.toString() + " '" + text.text + "'"
    }
}