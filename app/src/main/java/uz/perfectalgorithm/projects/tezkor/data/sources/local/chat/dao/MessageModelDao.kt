package uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.dao

import androidx.room.Dao
import androidx.room.Query
import uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.entity.MessageModel

/**
 * Created by Davronbek Raximjanov on 16.07.2021
 **/

@Dao
interface MessageModelDao : BaseDao<MessageModel> {
    @Query("SELECT * FROM messagemodel")
    fun getAllMessages(): List<MessageModel>
}