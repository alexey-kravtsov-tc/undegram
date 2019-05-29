package krafts.alex.backupgram.ui.messages

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import krafts.alex.backupgram.ui.messages.MessagesFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.item_message.view.*
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.Message
import java.io.File

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class TgMessagesRecyclerViewAdapter(
    private var mValues: List<Message>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<TgMessagesRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Message
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
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