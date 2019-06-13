package krafts.alex.backupgram.ui.utils

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class MinuteDataFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val date = Date(TimeUnit.SECONDS.toMillis(value.toLong()))
        return SimpleDateFormat("HH:mm").format(date)
    }
}

fun Int.display() = SimpleDateFormat("HH:mm").format(Date(this.toLong() * 1000))