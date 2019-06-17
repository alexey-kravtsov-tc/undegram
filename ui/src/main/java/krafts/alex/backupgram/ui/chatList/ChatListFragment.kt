package krafts.alex.backupgram.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsFragment
import krafts.alex.backupgram.ui.settings.SwipeToDeleteCallback

class ChatListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        val adapt = ChatsAdapter()

        val hideEdited = PreferenceManager
            .getDefaultSharedPreferences(activity)
            .getBoolean(SettingsFragment.HIDE_EDIT, false)

        BackApp.messages.getAllRemoved(hideEdited).observe(this, Observer {
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