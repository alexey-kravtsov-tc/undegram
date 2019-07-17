package krafts.alex.backupgram.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.fragment_chat_list.*
import krafts.alex.backupgram.ui.FragmentBase
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.viewModel

class ChatListFragment : FragmentBase() {

    private val viewModel: ChatListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chat_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = ChatsAdapter()

        viewModel.lastMessagesPerChat.observe(this, Observer {
            it?.let {
                placeholder.visibility = if (it.count() > 2) View.GONE else View.VISIBLE
                adapt.submitList(it)
                Crashlytics.setInt("chats_count", it.count())
            }
        })
        list?.adapter = adapt

        settings.reverseScroll.observe(this, Observer { reverse ->
            list?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, reverse)
        })
    }
}