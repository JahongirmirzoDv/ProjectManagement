package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification

import com.google.gson.annotations.SerializedName

data class NotificationCountResponse(
    @field:SerializedName("count")
    val count: Int
)
