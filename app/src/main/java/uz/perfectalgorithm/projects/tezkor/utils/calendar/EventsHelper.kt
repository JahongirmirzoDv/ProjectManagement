package uz.perfectalgorithm.projects.tezkor.utils.calendar

import androidx.collection.LongSparseArray
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import kotlin.math.pow

//fun getEvents(
//    fromTS: Long,
//    toTS: Long,
//    event: CalendarResponse.Event,
//    callback: (events: ArrayList<CalendarResponse.Event>) -> Unit
//) {
//    var events = getRepeatableEventsFor(
//        fromTS,
//        toTS,
//        event
//    ).toMutableList() as ArrayList<CalendarResponse.Event>
//
//    events = events
//        .asSequence()
//        .distinct()
//        .filterNot { it.repetitionExceptions.contains(DateTime(it.startTime).toString(Formatter.SIMPLE_DAY_PATTERN)) }
//        .toMutableList() as ArrayList<CalendarResponse.Event>
//
//    callback(events)
//}


fun getEvents(
    fromTS: Long,
    toTS: Long,
    event: CalendarResponse.Event,
    toAlarm: Boolean,
    callback: (events: ArrayList<CalendarResponse.Event>) -> Unit
) {
    val startTimes = LongSparseArray<Long>()
    val newEvents = ArrayList<CalendarResponse.Event>()

    startTimes.put(event.idLocalBase.toLong(), DateTime(event.startTime).seconds())
    var toTSCopy = toTS
    if (!event.untilDate.isNullOrEmpty() && DateTime(event.untilDate).seconds() < toTS) {
        toTSCopy = DateTime(event.untilDate).seconds()
    }
    if (event.repeatRule == 0 && event.repeat == App.instance.getString(R.string.every_week_)) {
        event.repeatRule = 2.0.pow(DateTime(event.startTime).dayOfWeek.toDouble()).toInt()
    }

    newEvents.addAll(
        getEventsRepeatingTillDateOrForever(
            fromTS,
            toTSCopy,
            startTimes,
            event,
            toAlarm
        )
    )

    callback(newEvents
        .asSequence()
        .distinct()
        .filterNot { it.repetitionExceptions.contains(DateTime(it.startTime).toString(Formatter.SIMPLE_DAY_PATTERN)) }
        .toMutableList() as ArrayList<CalendarResponse.Event>)
}

private fun getEventsRepeatingTillDateOrForever(
    fromTS: Long,
    toTS: Long,
    startTimes: LongSparseArray<Long>,
    event: CalendarResponse.Event,
    toAlarm: Boolean
): ArrayList<CalendarResponse.Event> {
    val original = event.copy()
    var eventCopy = event.copy()
    val events = ArrayList<CalendarResponse.Event>()
    while (DateTime(eventCopy.startTime).seconds() <= toTS) {
        if (DateTime(eventCopy.endTime).seconds() >= fromTS) {
            if (eventCopy.repeat.isXWeeklyRepetition()) {
                if (DateTime(eventCopy.startTime).dayOfWeek.isTsOnProperDay(eventCopy.repeatRule)) {
                    if (isOnProperWeek(startTimes, eventCopy)) {
                        eventCopy.copy().apply {
                            events.add(this)
                            if (toAlarm && events.size >= 2) {
                                return events
                            }
                        }
                    }
                }
            } else {
                eventCopy.copy().apply {
                    events.add(this)
                    if (toAlarm && events.size >= 2) {
                        return events
                    }
                }
            }
        }

        if (eventCopy.getIsAllDay()) {
            if (eventCopy.repeat.isXWeeklyRepetition()) {
                if (DateTime(eventCopy.endTime).seconds() >= toTS && DateTime(eventCopy.startTime).dayOfWeek.isTsOnProperDay(
                        eventCopy.repeatRule
                    )
                ) {
                    if (isOnProperWeek(startTimes, eventCopy)) {
                        eventCopy.copy().apply {
                            events.add(this)
                            if (toAlarm && events.size >= 2) {
                                return events
                            }
                        }
                    }
                }
            } else {
                val dayCode = Formatter.getDayCodeFromTS(fromTS)
                val endDayCode = DateTime(event.endTime).toString(Formatter.DAYCODE_PATTERN)
                if (dayCode == endDayCode) {
                    event.copy().apply {
                        events.add(this)
                        if (toAlarm && events.size >= 2) {
                            return events
                        }
                    }
                }
            }
        }

        val newStartTS = addIntervalTime(original, eventCopy)
        val newEndTS =
            newStartTS + (DateTime(original.endTime).millis - DateTime(original.startTime).millis)
        val startTime = DateTime(newStartTS).toString(Formatter.DAY_BASE_PATTERN)
        val endTime = DateTime(newEndTS).toString(Formatter.DAY_BASE_PATTERN)

        eventCopy = eventCopy.copy(
            endTime = endTime,
            startTime = startTime
        )
    }
    return events
}

//fun updateIsPastEvent(event: CalendarResponse.Event): Boolean {
//    val endTSToCheck =
//        if (DateTime(event.startTime).seconds() < getNowSeconds() && event.getIsAllDay()) {
//            Formatter.getDayEndTS(DateTime(event.startTime).toString(Formatter.DAYCODE_PATTERN))
//        } else {
//            Formatter.getDayEndTS(DateTime(event.startTime).toString(Formatter.DAYCODE_PATTERN))
//        }
//    return endTSToCheck < getNowSeconds()
//}


fun addIntervalTime(
    original: CalendarResponse.Event,
    event: CalendarResponse.Event
): Long {
    val oldStart = DateTime(event.startTime)
    val newStart: DateTime = when (event.repeat) {
        App.instance.getString(R.string.every_day_) -> {
            oldStart.plusDays(1)
        }
        App.instance.getString(R.string.every_week_) -> {
            oldStart.plusDays(1)
        }
        App.instance.getString(R.string.every_month_) -> {
            when (event.repeatRule) {
                REPEAT_SAME_DAY, REPEAT_NOT_SELECTED -> addMonthsWithSameDay(
                    oldStart,
                    original,
                    MONTH
                )
                REPEAT_ORDER_WEEKDAY -> addXthDayInterval(oldStart, original, false, MONTH)
                REPEAT_ORDER_WEEKDAY_USE_LAST -> addXthDayInterval(oldStart, original, true, MONTH)
                else -> oldStart.plusMonths(getRepeatInterval(event.repeat) / MONTH).dayOfMonth()
                    .withMaximumValue()
            }
        }
        App.instance.getString(R.string.every_year_) -> {
            when (event.repeatRule) {
                REPEAT_SAME_DAY, REPEAT_NOT_SELECTED -> addYearsWithSameDay(
                    oldStart, YEAR
                )
                REPEAT_ORDER_WEEKDAY -> addXthDayInterval(oldStart, original, false, YEAR)
                REPEAT_ORDER_WEEKDAY_USE_LAST -> addXthDayInterval(oldStart, original, true, YEAR)
                else -> addYearsWithSameDay(oldStart, YEAR)
            }
        }
        else -> oldStart.plusDays(1)
    }

    val newStartTS = newStart.millis

    return newStartTS
}

fun isOnProperWeek(startTimes: LongSparseArray<Long>, event: CalendarResponse.Event): Boolean {
    val initialWeekNumber = Formatter.getDateTimeFromTS(startTimes[event.idLocalBase.toLong()]!!)
        .withTimeAtStartOfDay().millis / (7 * 24 * 60 * 60 * 1000f)
    val currentWeekNumber =
        DateTime(event.startTime).withTimeAtStartOfDay().millis / (7 * 24 * 60 * 60 * 1000f)
    return (Math.round(initialWeekNumber) - Math.round(currentWeekNumber)) % (getRepeatInterval(
        event.repeat
    ) / WEEK) == 0
}

fun getRepeatInterval(repeat: String): Int {
    return when (repeat) {
        App.instance.getString(R.string.every_day_) -> {
            DAY
        }
        App.instance.getString(R.string.every_week_) -> {
            WEEK
        }
        App.instance.getString(R.string.every_month_) -> {
            MONTH
        }
        App.instance.getString(R.string.every_year_) -> {
            YEAR
        }
        else -> 0
    }
}

private fun String.isXWeeklyRepetition(): Boolean =
    this == App.instance.getString(R.string.every_week_)

private fun String.isXMonthlyRepetition(): Boolean =
    this == App.instance.getString(R.string.every_month_)

private fun String.isYearlyRepetition(): Boolean =
    this == App.instance.getString(R.string.every_year_)

// if an event should happen on 29th Feb. with Same Day yearly repetition, show it only on leap years
private fun addYearsWithSameDay(currStart: DateTime, repeatInterval: Int): DateTime {
    var newDateTime = currStart.plusYears(repeatInterval / YEAR)

    // Date may slide within the same month
    if (newDateTime.dayOfMonth != currStart.dayOfMonth) {
        while (newDateTime.dayOfMonth().maximumValue < currStart.dayOfMonth) {
            newDateTime = newDateTime.plusYears(repeatInterval / YEAR)
        }
        newDateTime = newDateTime.withDayOfMonth(currStart.dayOfMonth)
    }
    return newDateTime
}

// if an event should happen on 31st with Same Day monthly repetition, dont show it at all at months with 30 or less days
private fun addMonthsWithSameDay(
    currStart: DateTime,
    original: CalendarResponse.Event,
    repeatInterval: Int
): DateTime {

    var newDateTime = currStart.plusMonths(repeatInterval / MONTH)
    if (newDateTime.dayOfMonth == currStart.dayOfMonth) {
        return newDateTime
    }

    while (newDateTime.dayOfMonth().maximumValue < DateTime(original.startTime).dayOfMonth().maximumValue) {
        newDateTime = newDateTime.plusMonths(repeatInterval / MONTH)
        newDateTime = try {
            newDateTime.withDayOfMonth(currStart.dayOfMonth)
        } catch (e: Exception) {
            newDateTime
        }
    }
    return newDateTime
}

// handle monthly repetitions like Third Monday
private fun addXthDayInterval(
    currStart: DateTime,
    original: CalendarResponse.Event,
    forceLastWeekday: Boolean,
    repeatInterval: Int
): DateTime {
    val day = currStart.dayOfWeek
    var order = (currStart.dayOfMonth - 1) / 7
    var properMonth =
        currStart.withDayOfMonth(7).plusMonths(repeatInterval / MONTH).withDayOfWeek(day)
    var wantedDay: Int

    // check if it should be for example Fourth Monday, or Last Monday
    if (forceLastWeekday && (order == 3 || order == 4)) {
        val originalDateTime = DateTime(original.startTime)
        val isLastWeekday = originalDateTime.monthOfYear != originalDateTime.plusDays(7).monthOfYear
        if (isLastWeekday)
            order = -1
    }

    if (order == -1) {
        wantedDay =
            properMonth.dayOfMonth + ((properMonth.dayOfMonth().maximumValue - properMonth.dayOfMonth) / 7) * 7
    } else {
        wantedDay = properMonth.dayOfMonth + (order - (properMonth.dayOfMonth - 1) / 7) * 7
        while (properMonth.dayOfMonth().maximumValue < wantedDay) {
            properMonth =
                properMonth.withDayOfMonth(7).plusMonths(repeatInterval / MONTH).withDayOfWeek(day)
            wantedDay = properMonth.dayOfMonth + (order - (properMonth.dayOfMonth - 1) / 7) * 7
        }
    }

    return properMonth.withDayOfMonth(wantedDay)
}