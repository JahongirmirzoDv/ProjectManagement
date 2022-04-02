package uz.perfectalgorithm.projects.tezkor.domain.home.calendar

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.DayMonthly
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse

interface CalendarRepository {
    fun getMonthWithoutEvent(monthCode: String): Flow<Result<List<DayMonthly>>>
    fun getDayEvents(day: DateTime, context: Context): Flow<Result<List<CalendarResponse.Event>>>
    fun getWeekEvents(day: DateTime, context: Context): Flow<Result<List<CalendarResponse.Event>>>
    fun getMonthEvents(
        month: DateTime,
        context: Context
    ): Flow<Result<List<CalendarResponse.Event>>>

    fun getDayEventsStaff(
        day: DateTime,
        staffId: Int
    ): Flow<Result<List<CalendarResponse.Event>>>

    fun getWeekEventsStaff(
        day: DateTime,
        staffId: Int
    ): Flow<Result<List<CalendarResponse.Event>>>

    fun getMonthEventsStaff(
        month: DateTime,
        staffId: Int
    ): Flow<Result<List<CalendarResponse.Event>>>
}