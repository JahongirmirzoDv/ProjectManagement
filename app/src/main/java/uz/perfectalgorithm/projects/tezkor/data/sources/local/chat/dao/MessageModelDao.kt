package uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.dao

import androidx.room.Dao
import androidx.room.Query
import uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.entity.MessageModel



@Dao
interface MessageModelDao : BaseDao<MessageModel> {
    @Query("SELECT * FROM messagemodel")
    fun getAllMessages(): List<MessageModel>
}