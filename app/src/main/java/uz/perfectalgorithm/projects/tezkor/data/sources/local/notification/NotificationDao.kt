package uz.perfectalgorithm.projects.tezkor.data.sources.local.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setDate(date: LastUpdateDate)

    @Query("SELECT date FROM last_update_date WHERE company_id = :companyId and id_notification = 1")
    fun getDate(companyId: Int): String

}