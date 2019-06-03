package krafts.alex.backupgram.ui.messages

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.item_message.view.*
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.Message
import java.io.File

class TgMessagesRecyclerViewAdapter(
    private var mValues: List<Message>
) : RecyclerView.Adapter<TgMessagesRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Message
            val action = MessagesFragmentDirections.actionDialogDetails(item.chatId)
            Navigation.findNavController(v).navigate(action)
        }
    }

    fun setAll(items: List<Message>) {
        mValues = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.name.text = item.user?.let { it.firstName + " " + it.lastName }

        item.user?.photoBig?.let {
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
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.name
        val message: TextView = view.message
        val avatar: ImageView = view.avatar

        override fun toString(): String {
            return super.toString() + " '" + message.text + "'"
        }
    }
}