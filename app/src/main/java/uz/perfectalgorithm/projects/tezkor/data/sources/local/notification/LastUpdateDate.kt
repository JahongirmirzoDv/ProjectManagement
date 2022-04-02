package uz.perfectalgorithm.projects.tezkor.data.sources.local.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_update_date")
data class LastUpdateDate(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id_notification")
    val id: Int = 1,
    @ColumnInfo(name = "company_id")
    val companyId: Int,
    @ColumnInfo(name = "date")
    val date: String
)