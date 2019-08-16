package krafts.alex.backupgram.ui.utils

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MinuteDataFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val date = Date(TimeUnit.SECONDS.toMillis(value.toLong()))
        return SimpleDateFormat("HH:mm").format(date)
    }
}

fun Int.display() = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(this.toLong() * 1000))

fun Long.toPeriodString(): String {
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val second = this % 60

    return when {
        hours > 0 -> "${hours}h ${String.format("%02d", minutes)}m ${second}s"
        minutes > 0 -> "${minutes}m ${String.format("%02d", second)}s"
        else -> "${second}s"
    }
}