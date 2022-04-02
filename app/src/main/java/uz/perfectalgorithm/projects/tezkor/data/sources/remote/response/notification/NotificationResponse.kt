package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification

import com.google.gson.annotations.SerializedName

class NotificationResponse {

    data class Result(
        @SerializedName("data")
        val data: NotificationPageData,
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("message")
        val message: String
    )

    data class NotificationPageData(
        @SerializedName("pages_count")
        val pagesCount: Int,
        @SerializedName("all_comments_count")
        val commentsCount: Int,
        @SerializedName("notifications_list")
        val notificationsList: List<NotificationData>
    )

    data class NotificationData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("process")
        val process: String,
        @SerializedName("time")
        val time: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("repeat")
        val repeat: String,
    )
}