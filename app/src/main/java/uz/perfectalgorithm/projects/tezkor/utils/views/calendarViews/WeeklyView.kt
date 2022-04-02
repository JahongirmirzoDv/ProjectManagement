package uz.perfectalgorithm.projects.tezkor.utils.views.calendarViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import uz.perfectalgorithm.projects.tezkor.R

class WeeklyView(context: Context, attrs: AttributeSet, defStyle: Int) :
    View(context, attrs, defStyle) {
    private val ROWS_CNT = 24
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var daysCount = 7

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        paint.color = ContextCompat.getColor(context, R.color.disabled_color)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rowWeight = resources.getDimension(R.dimen.week_view_height)
        for (i in 0..ROWS_CNT) {
            val y = rowWeight * i.toFloat()
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
        }
        val rowWidth = width / daysCount.toFloat()
        for (i in 0 until daysCount) {
            val x = rowWidth * i.toFloat()
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
        }
    }


}
