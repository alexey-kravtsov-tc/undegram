package krafts.alex.backupgram.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.data.Entry
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.FragmentBase
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.backupgram.ui.utils.SwipeToDeleteCallback
import krafts.alex.backupgram.ui.viewModel
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.User
import krafts.alex.tg.repo.SessionRepository
import org.kodein.di.generic.instance
import java.io.File

class ChatFragment : FragmentBase() {

    private val sessionRepository: SessionRepository by instance()

    private val viewModel: ChatViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chat, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings.animations.observe(this, Observer { animate ->
            sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)?.apply { duration = 200 }
                ?.takeIf { animate == true }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            val args = ChatFragmentArgs.fromBundle(it)
            val user = BackApp.users.get(args.chatId.toInt())
            val chat = BackApp.chats.get(args.chatId)

            activity?.toolbar?.title = chat?.title ?: "${user?.firstName} ${user?.lastName}"

            (chat?.photoBig ?: user?.photoBig)?.let {
                if (it.downloaded)
                    Picasso.get()
                        .load(File(it.localPath))
                        .placeholder(R.drawable.ic_users)
                        .transform(CircleTransform())
                        .into(avatar)
            }

            postponeEnterTransition()

            chat?.let { setChatInfo(chat) }
            user?.let { setTimeTable(user) }

            showMessages(view, args.chatId)

            sessionRepository.getSessionsForUser(args.chatId.toInt()).observe(this, Observer {

                val values = ArrayList<Entry>()

                it?.forEach {
                    values.add(Entry(it.start.toInterval().toFloat(), 0F))
                    for (x in it.start.toInterval() + 1 until it.expires.toInterval()) {
                        values.add(Entry(x.toFloat(), 1F))
                    }
                    values.add(Entry(it.expires.toInterval().toFloat(), 0F))
                }

                chart.showValues(values)

            })
            //            notifyDeleted.setOnClickListener {
            //                BackApp.users.updateNotificationsSettings(args.chatId.toInt(), true)
            //            }
        }
        startPostponedEnterTransition()
    }

    private fun Int.toInterval() = this.toLong()

    private fun showMessages(
        view: View, chatId: Long
    ) {
        val adapt = MessagesAdapter(this)

        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(view.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapt.removeAt(viewHolder.adapterPosition) {
                    viewModel.deleteMessagePermanently(this)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(list)

        viewModel.pagedListForChat(chatId).observe(this, Observer {
            it?.let {
                adapt.submitList(it)
                placeholder.visibility = if (it.count() > 0) View.GONE else View.VISIBLE
            }
        })

        with(list) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapt
        }
    }

    private fun setChatInfo(chat: Chat) {
        if (chat.id == 1L) {
            analytics.logEvent("demo_user", null)
        }
        total.text = getString(R.string.chat_total_stub)
        yesterday.text = ""
        today.text = ""
        chart.visibility = View.GONE
    }

    private fun setTimeTable(user: User) {

        val timeYesterday = sessionRepository.getYesterdayTotal(user.id)
        val timeToday = sessionRepository.getTodayTotal(user.id)

        //TODO: use proper time formatting
        if (timeYesterday + timeToday > 60) {
            total.text = getString(R.string.recorded_time_online)
            yesterday.text = timeYesterday.let {
                "yesterday: ${it / 3600} h ${it % 3600 / 60} m "
            }
            today.text = timeToday.let {
                "today: ${it / 3600} h ${it % 3600 / 60} m "
            }
            chart.visibility = View.VISIBLE
        } else {
            total.text = getString(R.string.collecting_data)
            yesterday.text = ""
            today.text = ""
        }
    }
}