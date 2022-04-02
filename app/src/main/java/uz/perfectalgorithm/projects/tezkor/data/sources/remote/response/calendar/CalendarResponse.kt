package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.utils.calendar.FLAG_ALL_DAY
import uz.perfectalgorithm.projects.tezkor.utils.calendar.FLAG_IS_PAST_EVENT
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.addBitIf

class CalendarResponse {
    data class Result(
        @field: SerializedName("data")
        val events: ArrayList<Event>,
        @field: SerializedName("error")
        val error: List<Any>,
        @field: SerializedName("message")
        val message: String,
        @field: SerializedName("success")
        val success: Boolean
    )

    @Entity(tableName = "events")
    @Parcelize
    data class Event(
        @field: SerializedName("end_time")
        var endTime: String,
        @field: SerializedName("id")
        val idType: Int,
        @field: SerializedName("start_time")
        var startTime: String,
        @field: SerializedName("title")
        val title: String,
        @field: SerializedName("type")
        val type: String,
        @field:Expose
        @ColumnInfo(name = "flags") var flags: Int = 0,
        @field: SerializedName("repeat")
        @ColumnInfo(name = "repeat") var repeat: String = "",
        @field: SerializedName("reminders")
        @ColumnInfo(name = "reminders") var reminder: ArrayList<Int> = ArrayList(),
        @field: SerializedName("until_date")
        @ColumnInfo(name = "until_date") var untilDate: String? = "",
        @field: SerializedName("repeat_exceptions")
        @ColumnInfo(name = "repeat_exceptions") var repetitionExceptions: ArrayList<String> = ArrayList(),
        @field: SerializedName("repeat_rule")
        @ColumnInfo(name = "repeat_rule") var repeatRule: Int = 0,
        @field:Expose
        @ColumnInfo(name = "company_id") var companyId: Int = 0,
        @field:Expose
        @PrimaryKey(autoGenerate = true)
        var idLocalBase: Int = 0,
    ) : Parcelable {
        fun getIsAllDay() = flags and FLAG_ALL_DAY != 0

        var isPastEvent: Boolean
            get() = flags and FLAG_IS_PAST_EVENT != 0
            set(isPastEvent) {
                flags = flags.addBitIf(isPastEvent, FLAG_IS_PAST_EVENT)
            }

        override fun hashCode(): Int {
            return super.hashCode()
        }


        override fun equals(other: Any?): Boolean {
            return if (other is Event) {
                other.type == type && other.idType == idType
            } else {
                false
            }
        }
    }

//    @Entity(
//        foreignKeys = [ForeignKey(
//            entity = Event::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("server_id"),
//            onDelete = ForeignKey.CASCADE
//        )], tableName = "reminder"
//    )
//    @Parcelize
//    data class Reminder(
//        @PrimaryKey(autoGenerate = false)
//        @field: SerializedName("id")
//        @ColumnInfo(name = "id")
//        val id: Int,
//        @field: SerializedName("minutes")
//        @ColumnInfo(name = "minutes")
//        private val minutes: Int,
//        @field: SerializedName("server_id")
//        @ColumnInfo(name = "server_id")
//        private val serverId: String,
//        @field: SerializedName("calendar_id")
//        @ColumnInfo(name = "calendar_id")
//        private val calendarId: String
//    ) : Parcelable
//
//    @Parcelize
//    data class EventWithReminders(
//        @Embedded val event: Event,
//        @Relation(
//            parentColumn = "id",
//            entityColumn = "server_id"
//        ) val reminders: List<Reminder> = ArrayList(),
//    ) : Parcelable

}