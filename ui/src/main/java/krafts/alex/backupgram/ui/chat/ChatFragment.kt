package krafts.alex.backupgram.ui.chat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.settings.SettingsFragment
import krafts.alex.backupgram.ui.utils.SwipeToDeleteCallback
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.backupgram.ui.utils.MinuteDataFormatter
import java.io.File

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getBoolean(SettingsFragment.ANIMATIONS, false)
        ) {
            sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)?.apply { duration = 200 }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = MessagesAdapter(this).apply { setHasStableIds(true) }

        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(view.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapt.removeAt(viewHolder.adapterPosition) {
                    BackApp.messages.deletePermanently(this)
                }
            }
        })

        with(list) {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapt
            itemTouchHelper.attachToRecyclerView(list)
        }

        chart.apply {
            setPinchZoom(true)
            isDragEnabled = true
            isScaleXEnabled = true
            isScaleYEnabled = false
            description.isEnabled = false

            axisLeft.apply {
                legend.isEnabled = false
                setDrawGridLines(false)
                axisMaximum = 1F
                axisMinimum = 0.1F
                isEnabled = false
            }

            axisRight.apply {
                isEnabled = false
            }

            xAxis.apply {
                valueFormatter = MinuteDataFormatter()
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setVisibleXRange(60F, 60 * 12f)
                labelCount = 6

                isGranularityEnabled = true
                granularity = 60F

                textColor = ContextCompat.getColor(context, R.color.colorAccent)
            }

        }

        arguments?.let {
            val args = ChatFragmentArgs.fromBundle(it)

            val user = BackApp.users.get(args.chatId.toInt())
            val chat = BackApp.chats.get(args.chatId)
            val title = chat?.title ?: "${user?.firstName} ${user?.lastName}"

            activity?.toolbar?.title = title
            (chat?.photoBig ?: user?.photoBig)?.let {
                if (it.downloaded)
                    Picasso.get()
                        .load(File(it.localPath))
                        .placeholder(R.drawable.ic_users)
                        .transform(CircleTransform())
                        .into(avatar)
            }

            total.text = "Some info about chat"
            yesterday.text = ""
            today.text = ""
            chart.visibility = View.GONE


            user?.let {
                val timeYesterday = BackApp.sessions.getYesterdayTotal(user.id)
                val timeToday = BackApp.sessions.getTodayTotal(user.id)

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
                }
            }

            notifyDeleted.setOnClickListener {
                BackApp.users.updateNotificationsSettings(args.chatId.toInt(), true)
            }

            val hideEdited = PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getBoolean(SettingsFragment.HIDE_EDIT, false)

            BackApp.messages.getRemovedForChat(args.chatId, hideEdited).observe(this, Observer {
                it?.let {
                    adapt.setAll(it)
                    placeholder.visibility = if (it.count() > 0) View.GONE else View.VISIBLE
                }

            })
            BackApp.sessions.getSessionsForUser(args.chatId.toInt()).observe(this, Observer {

                val values = ArrayList<Entry>()

                it?.forEach {
                    values.add(Entry(it.start.toInterval().toFloat(), 0F))
                    for (x in it.start.toInterval() + 1 until it.expires.toInterval()) {
                        values.add(Entry(x.toFloat(), 1F))
                    }
                    values.add(Entry(it.expires.toInterval().toFloat(), 0F))
                }

                val set = LineDataSet(values, "online")
                set.apply {
                    color = Color.GREEN
                    fillColor = Color.GREEN
                    fillAlpha = 65

                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                    cubicIntensity = 1F
                }
                val data = LineData(set)

                chart.data = data

                chart.invalidate()

            })
        }
    }

    private fun Int.toInterval() = this.toLong()
}