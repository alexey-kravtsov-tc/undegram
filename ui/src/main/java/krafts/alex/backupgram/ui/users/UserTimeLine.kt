package krafts.alex.backupgram.ui.users

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import krafts.alex.backupgram.ui.R
import krafts.alex.tg.entity.Session

class UserTimeLine(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var source: List<Session> = emptyList()
    private var visibleRange = 60 * 60  // 1 hour

    private var minTime = 0
    private var scale = 180F

    private val paint = Paint()
    private val paintBorder = Paint()
    private val paintHour = Paint()

    private val timelineHeight = context.resources.getDimension(R.dimen.timeline_height)
    private val hourlineHeight = context.resources.getDimension(R.dimen.hour_line_height)

    init {
        paint.color = ContextCompat.getColor(context, R.color.colorOnlineTimeLine)
        paint.style = Paint.Style.FILL

        paintBorder.color = ContextCompat.getColor(context, R.color.colorForegroundTimeLine)
        paintBorder.style = Paint.Style.FILL_AND_STROKE

        paintHour.color = Color.BLACK
        paintHour.style = Paint.Style.FILL_AND_STROKE
    }

    fun showTimeline(list: List<Session>, start: Int, finish: Int) {
        visibleRange = finish - start
        minTime = start
        source = list.filter { it.expires > minTime }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec), //342
            MeasureSpec.getSize(heightMeasureSpec)
        )
        scale = visibleRange.toFloat() / MeasureSpec.getSize(widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawRect(0F, timelineHeight, visibleRange / scale, 0F, paintBorder)
            source.forEach {
                drawRect(
                    (it.start).toX(),
                    timelineHeight,
                    (if (it.expires - it.start > scale) {
                        it.expires
                    } else {
                        it.start + scale.toInt()
                    }).toX(),
                    0F,
                    paint
                )
            }
            drawTimeline(hourlineHeight, 3600)
            drawTimeline(timelineHeight, 3600 * 24, 3600 * 21) //TODO: GMT OFFSET
        }
        super.onDraw(canvas)
    }

    private fun Canvas?.drawTimeline(height: Float, step: Int, min: Int = step) {
        var timeLineX = min - minTime % step
        while (timeLineX < visibleRange) {
            this?.drawRect(
                timeLineX / scale, 0F, timeLineX / scale + 1F, height,
                paintHour
            )
            timeLineX += step
        }
    }

    private fun Int.toX() = (this - minTime) / scale
}