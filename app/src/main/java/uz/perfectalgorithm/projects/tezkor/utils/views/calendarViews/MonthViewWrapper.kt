package uz.perfectalgorithm.projects.tezkor.utils.views.calendarViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.DayMonthly
import uz.perfectalgorithm.projects.tezkor.utils.calendar.COLUMN_COUNT
import uz.perfectalgorithm.projects.tezkor.utils.calendar.ROW_COUNT
import uz.perfectalgorithm.projects.tezkor.utils.views.onGlobalLayout

class MonthViewWrapper(context: Context, attrs: AttributeSet, defStyle: Int) :
    FrameLayout(context, attrs, defStyle) {
    private var dayWidth = 0f
    private var dayHeight = 0f
    private var weekDaysLetterHeight = 0
    private var horizontalOffset = 0
    private var wereViewsAdded = false
    private var isMonthDayView = true
    private var days = ArrayList<DayMonthly>()
    private var inflater: LayoutInflater
    private var monthView: MonthView
    private var dayClickCallback: ((day: DayMonthly) -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        val normalTextSize = resources.getDimensionPixelSize(R.dimen.normal_text_size).toFloat()
        weekDaysLetterHeight = 2 * normalTextSize.toInt()

        inflater = LayoutInflater.from(context)
        monthView = inflater.inflate(R.layout.month_view, this).findViewById(R.id.month_view)
        setupHorizontalOffset()

        onGlobalLayout {
            if (!wereViewsAdded && days.isNotEmpty()) {
                measureSizes()
                addClickableBackgrounds()
                monthView.updateDays(days, isMonthDayView)
            }
        }
    }

    fun updateDays(
        newDays: ArrayList<DayMonthly>,
        addEvents: Boolean,
        callback: ((DayMonthly) -> Unit)? = null
    ) {
        setupHorizontalOffset()
        measureSizes()
        dayClickCallback = callback

        days = newDays
        if (dayWidth != 0f && dayHeight != 0f) {
            addClickableBackgrounds()
        }
        isMonthDayView = !addEvents
        monthView.updateDays(days, isMonthDayView)
    }

    private fun setupHorizontalOffset() {
        horizontalOffset =
            0
    }

    private fun measureSizes() {
        dayWidth = (width - horizontalOffset) / 7f

        // avoid updating the height when coming back from a new event screen, when the keyboard was visible
        val newHeight = (height - weekDaysLetterHeight) / 6f
        if (newHeight > dayHeight) {
            dayHeight = (height - weekDaysLetterHeight) / 6f
        }
    }

    private fun addClickableBackgrounds() {
        removeAllViews()
        monthView = inflater.inflate(R.layout.month_view, this).findViewById(R.id.month_view)
        wereViewsAdded = true
        var curId = 0
        for (y in 0 until ROW_COUNT) {
            for (x in 0 until COLUMN_COUNT) {
                val day = days.getOrNull(curId)
                if (day != null) {
                    addViewBackground(x, y, day)
                }
                curId++
            }
        }
    }

    private fun addViewBackground(viewX: Int, viewY: Int, day: DayMonthly) {
        val xPos = viewX * dayWidth + horizontalOffset
        val yPos = viewY * dayHeight + weekDaysLetterHeight

        inflater.inflate(R.layout.month_view_background, this, false).apply {
            if (isMonthDayView) {
                background = null
            }

            layoutParams.width = dayWidth.toInt()
            layoutParams.height = dayHeight.toInt()
            x = xPos
            y = yPos
            setOnClickListener {
                dayClickCallback?.invoke(day)

                if (isMonthDayView) {
                    monthView.updateCurrentlySelectedDay(viewX, viewY)
                }
            }
            addView(this)
        }
    }

    fun togglePrintMode() {
        monthView.togglePrintMode()
    }
}
