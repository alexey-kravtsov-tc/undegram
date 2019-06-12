package krafts.alex.backupgram.ui.chat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import kotlinx.android.synthetic.main.fragment_chat.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.MinuteDataFormatter
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Date

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapt = MessagesAdapter(emptyList())

        chart.apply {
            setPinchZoom(true)
            isDragEnabled = true
            isScaleXEnabled = true
            isScaleYEnabled = false
            description.isEnabled = false

            axisLeft.apply {
                legend.isEnabled = false
                valueFormatter = DefaultValueFormatter(1)
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

            }

        }

        arguments?.let {
            val args = ChatFragmentArgs.fromBundle(it)
            textView.text = BackApp.chats.get(args.chatId)?.title

            notifyDeleted.setOnClickListener {
                BackApp.users.updateNotificationsSettings(args.chatId.toInt(), true)
            }

            BackApp.messages.getRemovedForChat(args.chatId).observe(this, Observer {
                it?.let { adapt.setAll(it) }
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

                val builder = StringBuilder()
                it?.forEach {
                    builder.appendln("${it.start.display()} - ${it.expires.display()}")
                }
                sessions.text = builder.toString()
            })
        }

        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = adapt
        }
    }

    private fun Int.toInterval() = this.toLong()

    private fun Int.display() = SimpleDateFormat("HH:mm:ss").format(Date(this.toLong() * 1000))
}

