package krafts.alex.backupgram.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dialog_details.*
import krafts.alex.backupgram.ui.messages.MessagesAdapter

class DialogDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = MessagesAdapter(emptyList()) //TODO: listadapter paging

        arguments?.let {
            val args = DialogDetailsFragmentArgs.fromBundle(it)
            textView.text = BackApp.chats.get(args.chatId)?.title
            BackApp.messages.getRemovedForChat(args.chatId).observe(this, Observer {
                it?.let { adapt.setAll(it) }
            })
        }

        // Set the adapter
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = adapt
        }
    }
}
