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
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsFragment
import krafts.alex.backupgram.ui.settings.SettingsRepo
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import krafts.alex.backupgram.ui.viewModel
import org.kodein.di.generic.instance

class ChatListFragment : Fragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()

    private val viewModel: ChatListViewModel by viewModel()

    private val settings: SettingsRepo by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chat_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = ChatsAdapter()

        viewModel.lastMessagesPerChat.observe(this, Observer {
            it?.let {
                adapt.setAll(it)
                placeholder.visibility = if (it.count() > 2) View.GONE else View.VISIBLE
            }
        })
        list?.adapter = adapt

        settings.reverseSroll.observe(this, Observer { reverse ->
            list?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, reverse)
        })
    }
}