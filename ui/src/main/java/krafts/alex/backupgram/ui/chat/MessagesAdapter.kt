package krafts.alex.backupgram.ui.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.chat.edits.EditsAdapter
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.backupgram.ui.utils.display
import krafts.alex.tg.entity.Message
import java.io.File

class MessagesAdapter(
    private var values: List<Message>,
    private val fragment: Fragment
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

        holder.message.text = item.text
        holder.date.text = "${if (item.edited) "edited" else "deleted"} ${item.date.display()}"

        if (position == 0 || item.senderId != values[position - 1].senderId) {
            holder.name.text = item.user?.let { it.firstName + " " + it.lastName }

            item.user?.photoBig?.let {
                if (it.downloaded)
                    Picasso
                        .get()
                        .load(File(it.localPath))
                        .transform(CircleTransform())
                        .into(holder.avatar)
            }
        } else {
            holder.name.visibility = View.GONE
            holder.avatar.visibility = View.GONE
        }

        val editsAdapter = EditsAdapter(emptyList())
        item.edits?.observe(fragment, Observer {
            editsAdapter.setAll(it)
        })
        holder.editList.adapter = editsAdapter
        holder.editList.visibility = if (item.edited) View.VISIBLE else View.GONE
        holder.separator.visibility = if (item.edited) View.VISIBLE else View.GONE

        with(holder.view) {
            tag = item
        }
    }

    override fun getItemCount(): Int = values.size
}