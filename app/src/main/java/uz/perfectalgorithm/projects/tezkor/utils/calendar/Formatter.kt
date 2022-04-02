package uz.perfectalgorithm.projects.tezkor.utils.calendar

import android.content.Context
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R

object Formatter {
    const val DAYCODE_PATTERN = "YYYYMMdd"
    const val YEAR_PATTERN = "YYYY"
    private const val MONTH_PATTERN = "MMMM "
    private const val DAY_PATTERN = "d"
    private const val DAY_OF_WEEK_PATTERN = "EEE"
    const val DAY_BASE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    const val SIMPLE_DAY_PATTERN = "yyyy-MM-dd"
    const val SIMPLE_TIME_PATTERN = "yyyy-MM-dd HH:mm"
    private const val PATTERN_TIME_24 = "HH:mm"

    const val PATTERN_HOURS_24 = "HH:mm"
    const val NOTIFICATION_PATTERN = "yyyy-MM-dd-HH-mm-ss"

    private fun getDateFromCode(
        context: Context,
        dayCode: String,
        shortMonth: Boolean = false
    ): String {
        val dateTime = getDateTimeFromCode(dayCode)
        val day = dateTime.toString(DAY_PATTERN)
        val year = dateTime.toString(YEAR_PATTERN)
        val monthIndex = Integer.valueOf(dayCode.substring(4, 6))
        var month = getMonthName(context, monthIndex)
        if (shortMonth)
            month = month.substring(0, Math.min(month.length, 3))
        var date = "$month $day"
        if (year != DateTime().toString(YEAR_PATTERN))
            date += " $year"
        return date
    }


    private fun getDayTitle(
        context: Context,
        dayCode: String,
        addDayOfWeek: Boolean = true
    ): String {
        val date = getDateFromCode(context, dayCode)
        val dateTime = getDateTimeFromCode(dayCode)
        val day = dateTime.toString(DAY_OF_WEEK_PATTERN)
        return if (addDayOfWeek)
            "$date ($day)"
        else
            date
    }


    fun getDate(context: Context, dateTime: DateTime, addDayOfWeek: Boolean = true) =
        getDayTitle(context, getDayCodeFromDateTime(dateTime), addDayOfWeek)

    fun getFullDate(context: Context, dateTime: DateTime): String {
        val day = dateTime.toString(DAY_PATTERN)
        val year = dateTime.toString(YEAR_PATTERN)
        val monthIndex = dateTime.monthOfYear
        val month = getMonthName(context, monthIndex)
        return "$month $day $year"
    }

    fun getTodayCode() = getDayCodeFromTS(getNowSeconds())

    fun getDateTimeFromCode(dayCode: String): DateTime =
        DateTimeFormat.forPattern(DAYCODE_PATTERN).parseDateTime(dayCode)

    fun getLocalDateTimeFromCode(dayCode: String): DateTime =
        DateTimeFormat.forPattern(DAYCODE_PATTERN).withZone(DateTimeZone.getDefault())
            .parseLocalDate(dayCode).toDateTimeAtStartOfDay()

    fun getDayCodeFromDateTime(dateTime: DateTime): String = dateTime.toString(DAYCODE_PATTERN)


    fun getDateTimeFromTS(ts: Long) = DateTime(ts * 1000L, DateTimeZone.getDefault())

    fun getUTCDateTimeFromTS(ts: Long) = DateTime(ts * 1000L, DateTimeZone.getDefault())

    fun getMonthName(context: Context, id: Int): String =
        context.resources.getStringArray(R.array.months)[id - 1]


    fun getUTCDayCodeFromTS(ts: Long): String = getUTCDateTimeFromTS(ts).toString(DAYCODE_PATTERN)


    fun getDayCodeFromTS(ts: Long): String {
        val daycode = getDateTimeFromTS(ts).toString(DAYCODE_PATTERN)
        return if (daycode.isNotEmpty()) {
            daycode
        } else {
            "0"
        }
    }
}
