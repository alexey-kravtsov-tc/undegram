package krafts.alex.backupgram.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R

class ChatListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        val adapt = ChatsAdapter(emptyList())


        BackApp.messages.getAllRemoved().observe(this, Observer {
            it?.let { adapt.setAll(it) }
        })

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = adapt
            }
        }
        return view
    }

}