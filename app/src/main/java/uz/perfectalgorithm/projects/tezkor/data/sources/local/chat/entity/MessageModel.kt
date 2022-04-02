package uz.perfectalgorithm.projects.tezkor.data.sources.local.chat.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MessageModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var isRead: Boolean?,
    var sender: Int?,
    var messageType: String?,
    var answerFor: Int?,
    var message: String?,
    var timeUpdated: String?,
) : Serializable
