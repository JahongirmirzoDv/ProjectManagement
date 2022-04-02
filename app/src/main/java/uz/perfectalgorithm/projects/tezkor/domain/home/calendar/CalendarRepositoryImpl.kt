package uz.perfectalgorithm.projects.tezkor.domain.home.calendar

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.joda.time.DateTime
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local.calendar.dao.EventDao
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.DayMonthly
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.CalendarApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import javax.inject.Inject


/***
 * Abduraxmonov Abdulla 11/09/2021
 * Bu calendar qismi uchun repo qismi bu yerda optimallashtirish kerak
 * 1 context ui Layerda keladi shuni to'g'irlash kerak
 * 2 kelgan datani localga saqlashni optimallashtirish
 * 3 kelgan ui chiqarishni optimallashtirish
 * 4 eventlarni alarm qilishni optimallashtirish
 */


class CalendarRepositoryImpl @Inject constructor(
    private val calendarApi: CalendarApi,
    private val localBase: EventDao,
    private val storage: LocalStorage
) : CalendarRepository {

    override fun getMonthWithoutEvent(monthCode: String): Flow<Result<List<DayMonthly>>> = flow {
        val days = ArrayList<DayMonthly>(DAYS_CNT)
        val date = Formatter.getDateTimeFromCode(monthCode)
        val currentMonthDate = date.dayOfMonth().maximumValue
        val firstDayIndex = date.withDayOfMonth(1).dayOfWeek - 1

        val prevMothDays = date.minusMonths(1).dayOfMonth().maximumValue

        var value = prevMothDays - firstDayIndex + 1

        var isThisMonth = false
        var curDay = date
        var isToday: Boolean


        for (i in 0 until DAYS_CNT) {
            when {
                i < firstDayIndex -> {
                    isThisMonth = false
                    curDay = date.withDayOfMonth(1).minusMonths(1)
                }
                i == firstDayIndex -> {
                    value = 1
                    isThisMonth = true
                    curDay = date
                }
                value == currentMonthDate + 1 -> {
                    value = 1
                    isThisMonth = false
                    curDay = date.withDayOfMonth(1).plusMonths(1)
                }
            }
            val newDay = curDay.withDayOfMonth(value)
            val dayCode = Formatter.getDayCodeFromDateTime(newDay)
            isToday = DateTime.now().toString(Formatter.DAYCODE_PATTERN) == newDay.toString(
                Formatter.DAYCODE_PATTERN
            )

            val day = DayMonthly(
                value,
                isThisMonth,
                isToday,
                dayCode,
                ArrayList(),
                i
            )
            days.add(day)
            value++
        }
        emit(Result.success(days))
    }

    override fun getDayEvents(
        day: DateTime,
        context: Context
    ): Flow<Result<List<CalendarResponse.Event>>> = flow {
        try {
            val data = calendarApi.getDayEvents(day.year, day.monthOfYear, day.dayOfMonth)

            if (data.isSuccessful) data.body()?.let {

                val base: ArrayList<CalendarResponse.Event>


                it.events.forEach { event ->
                    val idLocalBase = localBase.getOneData(event.type, event.idType)


                    if (idLocalBase != null) {
                        event.idLocalBase = idLocalBase
                        event.companyId = storage.selectedCompanyId
                        localBase.updateData(event)
                        context.rescheduleReminder(event)
                    } else {
                        event.companyId = storage.selectedCompanyId
                        val id = localBase.addEvent(event).toInt()
                        event.idLocalBase = id
                        if (DateTime(event.startTime).isAfterNow || DateTime(event.untilDate).isAfterNow) {
                            context.nextEventReminder(event)
                        }
                    }
                }
                base = localBase.getOneTimeEventsFromTo(
                    day.toString(Formatter.DAY_BASE_PATTERN),
                    day.plusDays(1).minusSeconds(1).toString(Formatter.DAY_BASE_PATTERN),
                    storage.selectedCompanyId
                ) as ArrayList<CalendarResponse.Event>
                base.removeAll(it.events)
                val deleteEventsIds =
                    base.asSequence().mapNotNull { it.idLocalBase }.toMutableList()
                localBase.deleteEvents(deleteEventsIds)
                context.deleteEvents(deleteEventsIds)
                emit(
                    Result.success(
                        getRepeatDateByDay(
                            it.events,
                            day
                        )
                    )
                )
            } else {
                emit(Result.failure<List<CalendarResponse.Event>>(HttpException(data)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<CalendarResponse.Event>>(e))
        }
    }

    override fun getWeekEvents(
        day: DateTime,
        context: Context
    ): Flow<Result<List<CalendarResponse.Event>>> = flow {
        try {
            val data = calendarApi.getWeekEvents(day.year, day.monthOfYear, day.dayOfMonth)
            if (data.isSuccessful) data.body()?.let {
                it.events.forEach { event ->
                    val idLocalBase = localBase.getOneData(event.type, event.idType)

                    if (idLocalBase != null) {
                        event.idLocalBase = idLocalBase
                        event.companyId = storage.selectedCompanyId
                        localBase.updateData(event)
                        context.rescheduleReminder(event)
                    } else {
                        event.companyId = storage.selectedCompanyId
                        val id = localBase.addEvent(event).toInt()
                        event.idLocalBase = id
                        if (DateTime(event.startTime).isAfterNow || DateTime(event.untilDate).isAfterNow) {
                            context.nextEventReminder(event)
                        }
                    }
                }
                val base = localBase.getOneTimeEventsFromTo(
                    day.toString(Formatter.DAY_BASE_PATTERN),
                    day.plusDays(7).minusSeconds(1).toString(Formatter.DAY_BASE_PATTERN),
                    storage.selectedCompanyId
                ) as ArrayList<CalendarResponse.Event>
                base.removeAll(it.events)
                val deleteEventsIds =
                    base.asSequence().mapNotNull { it.idLocalBase }.toMutableList()
                localBase.deleteEvents(deleteEventsIds)
                context.deleteEvents(deleteEventsIds)
                emit(
                    Result.success(
                        getRepeatDateByWeek(
                            it.events,
                            day
                        )
                    )
                )
            } else {
                emit(Result.failure<List<CalendarResponse.Event>>(HttpException(data)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<CalendarResponse.Event>>(e))
        }
    }

    override fun getMonthEvents(
        month: DateTime,
        context: Context
    ): Flow<Result<List<CalendarResponse.Event>>> =
        flow {
            try {
                val data = calendarApi.getMonthEvents(month.year, month.monthOfYear)
                if (data.isSuccessful) data.body()?.let {

                    it.events.forEach { event ->
                        val idLocalBase = localBase.getOneData(event.type, event.idType)
                        if (idLocalBase != null) {
                            event.idLocalBase = idLocalBase
                            localBase.updateData(event)
                            context.rescheduleReminder(event)
                        } else {
                            event.companyId = storage.selectedCompanyId
                            val id = localBase.addEvent(event).toInt()
                            event.idLocalBase = id
                            if (DateTime(event.startTime).isAfterNow || DateTime(event.untilDate).isAfterNow) {
                                context.nextEventReminder(event)
                            }
                        }
                    }

                    val base = localBase.getOneTimeEventsFromTo(
                        month.toString(Formatter.DAY_BASE_PATTERN),
                        month.plusMonths(1).minusSeconds(1).toString(Formatter.DAY_BASE_PATTERN),
                        storage.selectedCompanyId
                    ) as ArrayList<CalendarResponse.Event>
                    base.removeAll(it.events)
                    val deleteEventsIds =
                        base.asSequence().mapNotNull { it.idLocalBase }.toMutableList()
                    localBase.deleteEvents(deleteEventsIds)
                    context.deleteEvents(deleteEventsIds)
                    val firstDayIndex = month.withDayOfMonth(1).dayOfWeek - 1

                    val prevMothDays = month.minusMonths(1).dayOfMonth().maximumValue
                    emit(
                        Result.success(
                            getRepeatDateByMonth(
                                it.events, if (firstDayIndex > 0) {
                                    val value = prevMothDays - firstDayIndex + 1
                                    month.minusMonths(1).withDayOfMonth(value)
                                } else {
                                    month
                                }
                            )
                        )
                    )
                } else {
                    emit(
                        Result.failure<List<CalendarResponse.Event>>(
                            HttpException(data)
                        )
                    )
                }
            } catch (e: Exception) {
                emit(Result.failure<List<CalendarResponse.Event>>(e))
            }
        }.flowOn(Dispatchers.IO)


    override fun getDayEventsStaff(
        day: DateTime,
        staffId: Int
    ): Flow<Result<List<CalendarResponse.Event>>> = flow {
        try {
            val data =
                calendarApi.getDayEventsStaff(day.year, day.monthOfYear, day.dayOfMonth, staffId)
            if (data.isSuccessful) data.body()?.let {
                emit(
                    Result.success(
                        getRepeatDateByDay(
                            it.events,
                            day
                        )
                    )
                )
            } else {
                emit(Result.failure<List<CalendarResponse.Event>>(HttpException(data)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<CalendarResponse.Event>>(e))
        }
    }

    override fun getWeekEventsStaff(
        day: DateTime,
        staffId: Int
    ): Flow<Result<List<CalendarResponse.Event>>> = flow {
        try {
            val data =
                calendarApi.getWeekEventsStaff(day.year, day.monthOfYear, day.dayOfMonth, staffId)
            if (data.isSuccessful) data.body()?.let {
                emit(
                    Result.success(
                        getRepeatDateByWeek(
                            it.events,
                            day
                        )
                    )
                )

            } else {
                emit(Result.failure<List<CalendarResponse.Event>>(HttpException(data)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<CalendarResponse.Event>>(e))
        }
    }


    override fun getMonthEventsStaff(
        month: DateTime,
        staffId: Int
    ): Flow<Result<List<CalendarResponse.Event>>> =
        flow {
            try {
                val data = calendarApi.getMonthEventsStaff(month.year, month.monthOfYear, staffId)
                if (data.isSuccessful) data.body()?.let {
                    val firstDayIndex = month.withDayOfMonth(1).dayOfWeek - 1

                    val prevMothDays = month.minusMonths(1).dayOfMonth().maximumValue

                    emit(
                        Result.success(
                            getRepeatDateByMonth(
                                it.events, if (firstDayIndex > 0) {
                                    val value = prevMothDays - firstDayIndex + 1
                                    month.minusMonths(1).withDayOfMonth(value)
                                } else {
                                    month
                                }
                            )
                        )
                    )
                } else {
                    emit(
                        Result.failure<List<CalendarResponse.Event>>(
                            HttpException(data)
                        )
                    )
                }
            } catch (e: Exception) {
                emit(Result.failure<List<CalendarResponse.Event>>(e))
            }
        }
}

fun getRepeatDateByMonth(
    repeatableList: ArrayList<CalendarResponse.Event>,
    startDayOfMonth: DateTime
): ArrayList<CalendarResponse.Event> {
    var events = ArrayList<CalendarResponse.Event>()

    repeatableList.forEach {
        var endOfMonth = startDayOfMonth.plusDays(42).minusSeconds(1)


        if (it.repeat == App.instance.getString(R.string.once_)) {
            events.add(it)
            return@forEach
        }

        if (!it.untilDate.isNullOrEmpty() && endOfMonth
                .isAfter(DateTime(it.untilDate))
        ) {
            endOfMonth = DateTime(it.untilDate)
        }
        getEvents(
            startDayOfMonth.seconds(),
            endOfMonth.seconds(),
            it, false
        ) { itemEvents ->
            events.addAll(itemEvents)
        }
    }
    return events
}


fun getRepeatDateByWeek(
    repeatableList: ArrayList<CalendarResponse.Event>,
    startDayOfWeek: DateTime
): ArrayList<CalendarResponse.Event> {
    var events = ArrayList<CalendarResponse.Event>()

    repeatableList.forEach {
        if (it.repeat == App.instance.getString(R.string.once_)) {
            events.add(it)
            return@forEach
        }
        var endOfWeek = startDayOfWeek.plusDays(7).minusSeconds(1)
        if (!it.untilDate.isNullOrEmpty() && startDayOfWeek.plusDays(7).minusSeconds(1)
                .isAfter(DateTime(it.untilDate))
        ) {
            endOfWeek = DateTime(it.untilDate)
        }
        getEvents(
            startDayOfWeek.seconds(),
            endOfWeek.seconds(),
            it, false
        ) { itemEvents ->
            events.addAll(itemEvents)
        }
    }
    return events
}

fun getRepeatDateByDay(
    repeatableList: ArrayList<CalendarResponse.Event>,
    day: DateTime
): ArrayList<CalendarResponse.Event> {
    var events = ArrayList<CalendarResponse.Event>()

    repeatableList.forEach {
        if (it.repeat == App.instance.getString(R.string.once_)) {
            events.add(it)
            return@forEach
        }
        var endOfWeek = day.plusDays(1).minusSeconds(1)
        if (!it.untilDate.isNullOrEmpty() && day.plusDays(1).minusSeconds(1)
                .isAfter(DateTime(it.untilDate))
        ) {
            endOfWeek = DateTime(it.untilDate)
        }
        getEvents(
            day.seconds(),
            endOfWeek.seconds(),
            it, false
        ) { itemEvents ->
            events.addAll(itemEvents)
        }
    }

    return events
}