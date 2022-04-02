package uz.perfectalgorithm.projects.tezkor.utils.views.calendarViews

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import androidx.core.content.ContextCompat
import org.joda.time.DateTime
import org.joda.time.Days
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.DayMonthly
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.MonthViewEvent
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.adjustAlpha
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.getContrastColor
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.hexColor
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import kotlin.math.abs

class MonthView(context: Context, attrs: AttributeSet, defStyle: Int) :
    View(context, attrs, defStyle) {
    private val BG_CORNER_RADIUS = 8f

    private var textPaint: Paint
    private var eventPaint: Paint
    private var eventTitlePaint: TextPaint
    private var gridPaint: Paint
    private var circleStrokePaint: Paint
    private var dayWidth = 0f
    private var dayHeight = 0f
    private var primaryColor = 0
    private var textColor = 0
    private var redTextColor = 0
    private var weekDaysLetterHeight = 0
    private var eventTitleHeight = 0
    private var currDayOfWeek = 0
    private var smallPadding = 0
    private var maxEventsPerDay = 0
    private var horizontalOffset = 0
    private var dimPastEvents = true
    private var highlightWeekends = false
    private var isPrintVersion = false
    private var isMonthDayView = false
    private var allEvents = ArrayList<MonthViewEvent>()
    private var bgRectF = RectF()
    private var dayTextRect = Rect()
    private var dayLetters = ArrayList<String>()
    private var days = ArrayList<DayMonthly>()
    private var dayVerticalOffsets = SparseIntArray()
    private var selectedDayCoords = Point(-1, -1)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        textColor = Color.BLACK
        redTextColor = ContextCompat.getColor(context, R.color.count_text_color_red)

        smallPadding = resources.displayMetrics.density.toInt()
        val normalTextSize = resources.getDimensionPixelSize(R.dimen.normal_text_size)
        val smallTextSize = resources.getDimensionPixelSize(R.dimen.calendar_view_text_size)
        weekDaysLetterHeight = normalTextSize * 2

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = normalTextSize.toFloat()
            textAlign = Paint.Align.CENTER
        }
        eventPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = smallTextSize.toFloat()
        }

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor.adjustAlpha(LOWER_ALPHA)
        }
        circleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = primaryColor
        }


        val smallerTextSize = resources.getDimensionPixelSize(R.dimen.smaller_text_size)
        eventTitleHeight = smallerTextSize
        eventTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = smallerTextSize.toFloat()
            textAlign = Paint.Align.LEFT
        }

        initWeekDayLetters()
        setupCurrentDayOfWeekIndex()
    }

    fun updateDays(newDays: ArrayList<DayMonthly>, isMonthDayView: Boolean) {
        this.isMonthDayView = isMonthDayView
        days.forEach {
            it.dayEvents.clear()
        }
        days = newDays
        initWeekDayLetters()
        setupCurrentDayOfWeekIndex()
        groupAllEvents()
        invalidate()
    }

    private fun groupAllEvents() {
        days.forEach {
            val day = it
            day.dayEvents.forEach {
                val event = it
                val lastEvent = allEvents.lastOrNull { it.id == event.idLocalBase }
                val daysCnt = getEventLastingDaysCount(event)
                val validDayEvent = isDayValid(event, day.code)

                //todo change if ((lastEvent == null || lastEvent.startDayIndex + daysCnt <= day.indexOnMonthView) && !validDayEvent)
                if ((lastEvent == null || lastEvent.startDayIndex + daysCnt <= day.indexOnMonthView) && !validDayEvent) {
                    val monthViewEvent = MonthViewEvent(
                        event.idLocalBase, event.title, event.startTime, day.indexOnMonthView,
                        daysCnt, day.indexOnMonthView, event.type
                    )
                    allEvents.add(monthViewEvent)
                }
            }
        }
        allEvents = allEvents.asSequence().sortedWith(
            compareBy({ -it.daysCnt },
                { it.startTS },
                { it.startDayIndex },
                { it.title })
        )
            .toMutableList() as ArrayList<MonthViewEvent>


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dayVerticalOffsets.clear()
        measureDaySize(canvas)

        if (!isMonthDayView) {
            drawGrid(canvas)
        }

        addWeekDayLetters(canvas)
        var curId = 0
        for (y in 0 until ROW_COUNT) {
            for (x in 0 until COLUMN_COUNT) {
                val day = days.getOrNull(curId)
                if (day != null) {
                    dayVerticalOffsets.put(
                        day.indexOnMonthView,
                        dayVerticalOffsets[day.indexOnMonthView] + weekDaysLetterHeight
                    )
                    val verticalOffset = dayVerticalOffsets[day.indexOnMonthView]
                    val xPos = x * dayWidth + horizontalOffset
                    val yPos = y * dayHeight + verticalOffset + 10
                    val xPosCenter = xPos + dayWidth / 2
                    val dayNumber = day.value.toString()

                    val textPaint = getTextPaint(day)
                    if (selectedDayCoords.x != -1 && x == selectedDayCoords.x && y == selectedDayCoords.y) {
                        canvas.drawCircle(
                            xPosCenter,
                            yPos + textPaint.textSize * 0.7f,
                            textPaint.textSize * 0.8f,
                            circleStrokePaint
                        )
                        if (day.isToday) {
                            textPaint.color = textColor
                        }
                    } else if (day.isToday && !isPrintVersion) {
                        canvas.drawCircle(
                            xPosCenter,
                            yPos + textPaint.textSize * 0.7f,
                            textPaint.textSize * 0.8f,
                            getCirclePaint(day)
                        )
                    }

                    // mark days with events with a dot
                    if (isMonthDayView && day.dayEvents.isNotEmpty()) {
                        getCirclePaint(day).getTextBounds(
                            dayNumber,
                            0,
                            dayNumber.length,
                            dayTextRect
                        )
                        val height = dayTextRect.height() * 1.25f
                        canvas.drawCircle(
                            xPosCenter,
                            yPos + height + textPaint.textSize / 2,
                            textPaint.textSize * 0.2f, Paint()
                        )
                    }

                    canvas.drawText(dayNumber, xPosCenter, yPos + textPaint.textSize, textPaint)
                    dayVerticalOffsets.put(
                        day.indexOnMonthView,
                        (verticalOffset + textPaint.textSize * 2).toInt()
                    )
                }
                curId++
            }
        }

        if (!isMonthDayView) {
            for (event in allEvents) {
                drawEvent(event, canvas)
            }
        }
    }

    private fun drawGrid(canvas: Canvas) {
        for (i in 0 until COLUMN_COUNT) {
            val lineX = i * dayWidth
            canvas.drawLine(lineX, 0f, lineX, canvas.height.toFloat(), gridPaint)
        }
        // horizontal lines
        canvas.drawLine(0f, 0f, canvas.width.toFloat(), 0f, gridPaint)
        for (i in 0 until ROW_COUNT) {
            canvas.drawLine(
                0f,
                i * dayHeight + weekDaysLetterHeight,
                canvas.width.toFloat(),
                i * dayHeight + weekDaysLetterHeight,
                gridPaint
            )
        }
    }

    private fun addWeekDayLetters(canvas: Canvas) {
        for (i in 0 until COLUMN_COUNT) {
            val xPos = horizontalOffset + (i + 1) * dayWidth - dayWidth / 2
            var weekDayLetterPaint = textPaint
            if (i == currDayOfWeek && !isPrintVersion) {
                //color current week
                weekDayLetterPaint = getColoredPaint(Color.BLUE)
            } else if (highlightWeekends) {
                weekDayLetterPaint = getColoredPaint(redTextColor)
            }
            canvas.drawText(dayLetters[i], xPos, weekDaysLetterHeight * 0.7f, weekDayLetterPaint)
        }
    }


    private fun measureDaySize(canvas: Canvas) {
        dayWidth = (canvas.width - horizontalOffset) / 7f
        dayHeight = (canvas.height - weekDaysLetterHeight) / ROW_COUNT.toFloat()
        val availableHeightForEvents = dayHeight.toInt() - weekDaysLetterHeight
        maxEventsPerDay = availableHeightForEvents / eventTitleHeight
    }

    private fun drawEvent(event: MonthViewEvent, canvas: Canvas) {
        var verticalOffset = 0
        for (i in 0 until event.daysCnt.coerceAtMost(7 - event.startDayIndex % 7)) {
            verticalOffset = Math.max(verticalOffset, dayVerticalOffsets[event.startDayIndex + i])
        }
        val xPos = event.startDayIndex % 7 * dayWidth + horizontalOffset
        val yPos = (event.startDayIndex / 7) * dayHeight + 16
        val xPosCenter = xPos + dayWidth / 2

        if (verticalOffset - eventTitleHeight * 2 > dayHeight) {
            val paint = getTextPaint(days[event.startDayIndex])
            paint.color = textColor
            canvas.drawText(
                "...",
                xPosCenter,
                yPos + verticalOffset - eventTitleHeight / 2 - 14,
                paint
            )
            return
        }

        // event background rectangle
        val backgroundY = yPos + verticalOffset
        val bgLeft = xPos + smallPadding
        val bgTop = backgroundY + smallPadding - eventTitleHeight
        var bgRight = xPos - smallPadding + dayWidth * event.daysCnt
        val bgBottom = backgroundY + smallPadding * 2
        if (bgRight > canvas.width.toFloat()) {
            bgRight = canvas.width.toFloat() - smallPadding
            val newStartDayIndex = (event.startDayIndex / 7 + 1) * 7
            if (newStartDayIndex < 42) {
                val newEvent = event.copy(
                    startDayIndex = newStartDayIndex,
                    daysCnt = event.daysCnt - (newStartDayIndex - event.startDayIndex)
                )
                drawEvent(newEvent, canvas)
            }
        }

//        val startDayIndex = days[event.originalStartDayIndex]
//
//        val endDayIndex = days[min(event.startDayIndex + event.daysCnt - 1, 41)]

        bgRectF.set(bgLeft, bgTop, bgRight, bgBottom)
        val paint = Paint()
        paint.color = Color.parseColor(context.hexColor(event.type))
        canvas.drawRoundRect(
            bgRectF,
            BG_CORNER_RADIUS,
            BG_CORNER_RADIUS,
            paint
        )

        drawEventTitle(
            event,
            canvas,
            xPos,
            yPos + verticalOffset,
            bgRight - bgLeft + 50
        )


        for (i in 0 until Math.min(event.daysCnt, 7 - event.startDayIndex % 7)) {
            dayVerticalOffsets.put(
                event.startDayIndex + i,
                verticalOffset + eventTitleHeight + smallPadding * 2
            )
        }
    }

    private fun drawEventTitle(
        event: MonthViewEvent,
        canvas: Canvas,
        x: Float,
        y: Float,
        availableWidth: Float
    ) {
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.white)

        val ellipsized = TextUtils.ellipsize(
            event.title,
            eventTitlePaint,
            availableWidth,
            TextUtils.TruncateAt.END
        )
        canvas.drawText(
            event.title,
            0,
            ellipsized.length,
            x + smallPadding * 2,
            y - smallPadding, eventPaint
        )
    }

    private fun getTextPaint(startDay: DayMonthly): Paint {
        var paintColor = textColor
        if (!isPrintVersion) {
            if (startDay.isToday) {
                paintColor = primaryColor.getContrastColor()
            } else if (highlightWeekends) {
                paintColor = redTextColor
            }
        }

        if (!startDay.isThisMonth) {
            paintColor = paintColor.adjustAlpha(MEDIUM_ALPHA)
        }

        return getColoredPaint(paintColor)
    }

    private fun getColoredPaint(color: Int): Paint {
        val curPaint = Paint(textPaint)
        curPaint.color = color
        return curPaint
    }


    private fun getCirclePaint(day: DayMonthly): Paint {
        val curPaint = Paint(textPaint)
        var paintColor = Color.BLUE
        if (!day.isThisMonth) {
            paintColor = paintColor.adjustAlpha(MEDIUM_ALPHA)
        }
        curPaint.color = paintColor
        return curPaint
    }


    private fun initWeekDayLetters() {
        dayLetters = context.resources.getStringArray(R.array.week_days_short)
            .toMutableList() as ArrayList<String>
    }

    private fun setupCurrentDayOfWeekIndex() {
        if (days.firstOrNull { it.isToday && it.isThisMonth } == null) {
            currDayOfWeek = -1
            return
        }

        currDayOfWeek = DateTime().dayOfWeek - 1
    }

    // take into account cases when an event starts on the previous screen, subtract those days
    private fun getEventLastingDaysCount(event: CalendarResponse.Event): Int {
        val startDateTime = DateTime(event.startTime)
        val endDateTime = DateTime(event.endTime)
        val code = days.first().code
        val screenStartDateTime = Formatter.getDateTimeFromCode(code).toLocalDate()
        var eventStartDateTime = Formatter.getDateTimeFromTS(startDateTime.seconds()).toLocalDate()
        val eventEndDateTime = Formatter.getDateTimeFromTS(endDateTime.seconds()).toLocalDate()
        val diff = Days.daysBetween(screenStartDateTime, eventStartDateTime).days
        if (diff < 0) {
            eventStartDateTime = screenStartDateTime
        }

        val isMidnight =
            Formatter.getDateTimeFromTS(endDateTime.seconds()) == Formatter.getDateTimeFromTS(
                endDateTime.seconds()
            ).withTimeAtStartOfDay()
        val numDays = Days.daysBetween(eventStartDateTime, eventEndDateTime).days
        val daysCnt = if (numDays == 1 && isMidnight) 0 else numDays
        return abs(daysCnt + 1)
    }

    private fun isDayValid(event: CalendarResponse.Event, code: String): Boolean {
        val date = Formatter.getDateTimeFromCode(code)
        return event.startTime != event.endTime && DateTime(event.startTime) == Formatter.getDateTimeFromTS(
            date.seconds()
        ).withTimeAtStartOfDay()
    }

    fun togglePrintMode() {
        isPrintVersion = !isPrintVersion
        textColor = if (isPrintVersion) {
            Color.BLACK
        } else {
            Color.BLACK
        }
        textPaint.color = textColor
        gridPaint.color = textColor.adjustAlpha(LOWER_ALPHA)
        invalidate()
        initWeekDayLetters()
    }

    fun updateCurrentlySelectedDay(x: Int, y: Int) {
        selectedDayCoords = Point(x, y)
        invalidate()
    }
}
