package uz.perfectalgorithm.projects.tezkor.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.perfectalgorithm.projects.tezkor.data.sources.local.calendar.dao.EventDao
import uz.perfectalgorithm.projects.tezkor.data.sources.local.converters.ListToIntegerConverters
import uz.perfectalgorithm.projects.tezkor.data.sources.local.converters.ListToJsonConverters
import uz.perfectalgorithm.projects.tezkor.data.sources.local.notification.LastUpdateDate
import uz.perfectalgorithm.projects.tezkor.data.sources.local.notification.NotificationDao
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse


@Database(
    entities = [CalendarResponse.Event::class, LastUpdateDate::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListToJsonConverters::class, ListToIntegerConverters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    abstract fun notificationDao(): NotificationDao
}