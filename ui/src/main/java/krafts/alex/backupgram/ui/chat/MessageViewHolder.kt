package krafts.alex.backupgram.ui.chat

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_message.view.*
import krafts.alex.backupgram.ui.chat.edits.EditsAdapter
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.backupgram.ui.utils.display
import krafts.alex.tg.entity.MessageFromUserWithEdits
import java.io.File

class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    val message: TextView = view.message
    private val date: TextView = view.date
    private val avatar: ImageView = view.avatar
    private val editList: RecyclerView = view.editList
    private val separator: View = view.separator

    fun bind(item: MessageFromUserWithEdits, needName: Boolean) {
        message.text = item.text
        date.text = "${if (item.edited) "edited" else "deleted"} ${item.date.display()}"

        if (needName) {
            name.text = item.user?.let { it.firstName + " " + it.lastName }

            item.user?.photoBig?.let {
                if (it.downloaded)
                    Picasso
                        .get()
                        .load(File(it.localPath))
                        .transform(CircleTransform())
                        .into(avatar)
            }
        } else {
            name.visibility = View.GONE
            avatar.visibility = View.GONE
        }

        val editsAdapter = EditsAdapter(emptyList())
        editsAdapter.setAll(item.edits)
        editList.adapter = editsAdapter
        editList.itemAnimator = null
        editList.visibility = if (item.edited) View.VISIBLE else View.GONE
        separator.visibility = if (item.edited) View.VISIBLE else View.GONE

        with(view) {
            tag = item
        }
    }
    fun clear() {
        name.text= ""
        message.text = ""
        date.text = ""
        editList.visibility = View.GONE
        separator.visibility = View.GONE
    }


    override fun toString(): String {
        return super.toString() + " '" + message.text + "'"
    }
}