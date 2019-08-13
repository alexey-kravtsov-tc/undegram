package krafts.alex.backupgram.ui.users

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import krafts.alex.tg.entity.Session
import java.util.concurrent.TimeUnit

class UserTimeLine(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var source: List<Session> = emptyList()
    private var visibleRange = 60 * 60  // 1 hour

    private var minTime = 0
    private var scale = 180F

    private val paint = Paint()
    private val paintBorder = Paint()

    init {
        paint.color = Color.GREEN
        paint.style = Paint.Style.FILL

        paintBorder.color = Color.GRAY
        paintBorder.style = Paint.Style.FILL_AND_STROKE
    }

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

    fun showTimeline(list: List<Session>, range : Int ) {
        visibleRange = range
        minTime = now() - visibleRange
        source = list.filter { it.expires > minTime }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec), //342
            MeasureSpec.getSize(heightMeasureSpec)
        )
        scale = visibleRange.toFloat() / MeasureSpec.getSize(widthMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(0F, 10F, visibleRange / scale, 0F, paintBorder)
        source.forEach {
            canvas?.drawRect(
                (it.start).toX(),
                10F,
                (if (it.expires-it.start > scale) {
                    it.expires
                } else {
                    it.start + scale.toInt()
                }).toX(),
                0F,
                paint
            )
        }
        super.onDraw(canvas)
    }

    private fun Int.toX() = (this - minTime) / scale


}