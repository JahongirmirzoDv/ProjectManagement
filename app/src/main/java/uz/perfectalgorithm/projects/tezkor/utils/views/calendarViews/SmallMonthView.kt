package uz.perfectalgorithm.projects.tezkor.utils.views.calendarViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.adding.getMonths
import uz.perfectalgorithm.projects.tezkor.utils.converter.dpToPx
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

class SmallMonthView(context: Context, attributeSet: AttributeSet, defStyle: Int) :
    View(context, attributeSet, defStyle) {

    private var paint: Paint
    private var dayWidth = 0f
    private var todayCirclePaint: Paint
    private var monthNamePaint: Paint
    var weekLetters = mutableListOf<String>()
    var firstDay = 0
    var isCurrentMonth = false
    var days = 31
    var monthName = "Oy"


    constructor(context: Context, attributeSet: AttributeSet) : this(
        context,
        attributeSet,
        0
    )

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.SmallMonthView, 0, 0)


        try {
            days = attributes.getInt(R.styleable.SmallMonthView_days, 31)
//            firstDay = attributes.getInt(R.styleable.SmallMonthView_first_day, 0)
            monthName = attributes.getString(R.styleable.SmallMonthView_monthName) ?: "Oy"
        } finally {
            attributes.recycle()
        }

        paint = Paint().apply {
            textSize = resources.getDimension(R.dimen.year_view_day_text_size)
            textAlign = Paint.Align.RIGHT
        }
        monthNamePaint = Paint().apply {
            textSize = resources.getDimension(R.dimen.year_view_month_text_size)
            textAlign = Paint.Align.CENTER
        }
        todayCirclePaint = Paint().apply {
            color = Color.YELLOW
            textAlign = Paint.Align.RIGHT
        }
//        weekNamePaint = Paint().apply {
//            color = Color.BLACK
//        }

        weekLetters = resources.getStringArray(R.array.week_days_short).toMutableList()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dayWidth = width / 7f
        var curId = 2 - firstDay

        canvas?.drawText(
            monthName,
            width / 2f,
            18f.dpToPx(),
            getMonthsNamePaint()
        )

        for (x in 1..7) {
            canvas?.drawText(
                weekLetters[x - 1],
                x * dayWidth - (dayWidth / 4),
                dayWidth + 44,
                getPaint(x)
            )
        }

        for (y in 2..7) {
            for (x in 1..7) {
                if (curId in 1..days) {
                    val paint = getPaint(x)
                    if (curId == DateTime.now().dayOfMonth && isCurrentMonth) {
                        canvas?.drawCircle(
                            x * dayWidth - dayWidth / 2,
                            y * dayWidth - dayWidth / 4 + 44,
                            dayWidth * 0.41f, todayCirclePaint
                        )
                    }
                    canvas?.drawText(
                        curId.toString(),
                        x * dayWidth - (dayWidth / 4),
                        y * dayWidth + 44,
                        paint
                    )
                }
                curId++
            }
        }
    }

    private fun getMonthsNamePaint(): Paint {
        return if (isCurrentMonth){
            monthNamePaint.apply {
                color = Color.RED
            }
        }else{
            monthNamePaint.apply {
                color = Color.BLACK
            }
        }
    }

    private fun getPaint(x: Int): Paint {
        val curPaint = paint
        if (x == 7) {
            curPaint.color = Color.RED
        } else {
            curPaint.color = Color.BLACK
        }
        return curPaint
    }

}