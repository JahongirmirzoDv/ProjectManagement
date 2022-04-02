package uz.perfectalgorithm.projects.tezkor.data.sources.local.calendar.dao

import androidx.room.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvents(events: List<CalendarResponse.Event>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvent(events: CalendarResponse.Event): Long

    @Update
    suspend fun updateData(event: CalendarResponse.Event)

    @Query("SELECT * FROM events")
    fun getAllEvents(): List<CalendarResponse.Event>

    @Query("SELECT * FROM events WHERE idLocalBase = :id")
    fun getEventWithId(id: Long): CalendarResponse.Event

    @Query("SELECT * FROM events WHERE endTime >= :fromTS AND startTime <= :toTS and startTime<=endTime and company_id==:companyId")
    fun getOneTimeEventsFromTo(
        fromTS: String,
        toTS: String,
        companyId: Int
    ): List<CalendarResponse.Event>

    @Query("DELETE FROM events WHERE type == :type AND idType == :idType")
    fun deleteData(type: String, idType: Int)

    @Query("SELECT idLocalBase FROM events WHERE type == :type AND idType == :idType limit 1")
    fun getOneData(type: String, idType: Int): Int?

    @Query("DELETE FROM events WHERE type == :type AND idType == :idType")
    fun deleteTimeEventsFromTo(type: String, idType: Int)

    @Query("DELETE FROM events WHERE idLocalBase IN (:ids)")
    fun deleteEvents(ids: List<Int>)

    @Query("DELETE FROM events ")
    fun deleteAllEvents()

//    @Transaction
//    @Query("SELECT * FROM EVENTS")
//    fun getEventWithReminders(): List<CalendarResponse.EventWithReminders>

}