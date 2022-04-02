package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName

data class OwnTaskListResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null,

    @field:SerializedName("data")
    val data: List<OwnTaskItem>? = null

)

data class OwnTaskItem(

    @field:SerializedName("performer__first_name")
    val performerFirstName: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("leader__first_name")
    val leaderFirstName: String? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("leader__last_name")
    val leaderLastName: String? = null,

    @field:SerializedName("performer__last_name")
    val performerLastName: String? = null
)
