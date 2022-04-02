package uz.perfectalgorithm.projects.tezkor.data.sources.local_models

import com.google.gson.annotations.SerializedName

data class NotificationChatModel(
    @SerializedName("chat_id")
    val chatId: Int,
    @SerializedName("sender_image")
    val senderImage: String,
    @SerializedName("sender_name")
    val senderName: String,
    @SerializedName("is_media")
    val isMedia: Boolean
)