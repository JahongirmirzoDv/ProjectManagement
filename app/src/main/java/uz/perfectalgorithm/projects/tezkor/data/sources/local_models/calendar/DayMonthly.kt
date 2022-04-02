package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar

import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse

data class DayMonthly(
    val value: Int,
    val isThisMonth: Boolean,
    val isToday: Boolean,
    val code: String,
    var dayEvents: ArrayList<CalendarResponse.Event>,
    var indexOnMonthView: Int
)
