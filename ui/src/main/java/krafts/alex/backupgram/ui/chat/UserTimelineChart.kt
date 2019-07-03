package krafts.alex.backupgram.ui.chat

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_chat.*
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.utils.MinuteDataFormatter

class UserTimelineChart(context: Context, attrs: AttributeSet) : LineChart(context, attrs) {
    fun showValues(values: ArrayList<Entry>) {
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

        this.data = data

        invalidate()
    }

    init {
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

            textColor = ContextCompat.getColor(
                context,
                R.color.colorAccent
            )
        }
    }
}