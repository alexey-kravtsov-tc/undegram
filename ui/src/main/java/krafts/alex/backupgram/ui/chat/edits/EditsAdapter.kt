package krafts.alex.backupgram.ui.chat.edits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.display
import krafts.alex.tg.entity.Edit

class EditsAdapter(
    private var values: List<Edit>
) : RecyclerView.Adapter<EditViewHolder>() {

    fun setAll(items: List<Edit>) {
        values = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edit, parent, false)
        return EditViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        val item = values[position]
        holder.text.text = item.text
        holder.date.text = item.date.display()
    }

    override fun getItemCount(): Int = values.size

}