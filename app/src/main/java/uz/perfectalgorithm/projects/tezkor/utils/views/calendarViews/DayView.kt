package uz.perfectalgorithm.projects.tezkor.utils.views.calendarViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.converter.dpToPx

class DayView(context: Context, attrs: AttributeSet, defStyle: Int) :
    View(context, attrs, defStyle) {
    private val ROWS_CNT = 24
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var dayHeight = 60f.dpToPx()

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        paint.color = ContextCompat.getColor(context, R.color.disabled_color)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0..ROWS_CNT) {
            val y = dayHeight * i.toFloat()
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
        }
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)
    }

    fun setDayRowHeight(dayHeight: Float) {
        this.dayHeight = dayHeight
        invalidate()
    }
}
