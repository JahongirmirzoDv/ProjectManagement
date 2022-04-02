package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar

data class DayModel(
    var month: Int = 0,
    var day: Int = 0,
    var year: Int = 0,
    var isEnable: Boolean = false,
    val value: Int,
    val isThisMonth: Boolean,
    var isToday: Boolean,
    val code: String,
    val weekOfYear: Int,
    var dayEvents: ArrayList<EventCalendar>,
    var indexOnMonthView: Int,
    var isWeekend: Boolean,
    var eventInfo: EventInfo
) {
    constructor() : this(
        7,
        1,
        2021,
        false,
        0,
        false,
        false,
        "",
        0,
        arrayListOf(),
        0,
        false,
        EventInfo()
    )
}