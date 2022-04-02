package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar

data class MonthViewEvent(
    val id: Int,
    val title: String,
    val startTS: String,
    val startDayIndex: Int,
    val daysCnt: Int,
    val originalStartDayIndex: Int,
    val type: String
)
