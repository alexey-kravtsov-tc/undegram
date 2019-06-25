package krafts.alex.backupgram.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_chat_list.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsFragment

class ChatListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = ChatsAdapter()

        val hideEdited = PreferenceManager
            .getDefaultSharedPreferences(activity)
            .getBoolean(SettingsFragment.HIDE_EDIT, false)

        BackApp.messages.getAllRemoved(hideEdited).observe(this, Observer {
            it?.let {
                adapt.setAll(it)
                placeholder.visibility = if (it.count() > 2) View.GONE else View.VISIBLE
            }
        })
        val reverse = PreferenceManager
            .getDefaultSharedPreferences(activity)
            .getBoolean(SettingsFragment.REVERSE_SCROLL, false)

        // Set the adapter
        list?.let {
            it.layoutManager = if (reverse) {
                LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            } else {
                LinearLayoutManager(context)
            }
            it.adapter = adapt
        }
    }
}