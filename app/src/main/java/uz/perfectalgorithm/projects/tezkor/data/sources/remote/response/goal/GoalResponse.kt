package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal

import com.google.gson.annotations.SerializedName

data class GoalResponse(

    @field:SerializedName("data")
    val data: List<GoalData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class GoalData(

    @field:SerializedName("leader")
    val leader: Leader? = null,

    @field:SerializedName("performer")
    val performer: Performer? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("spectators")
    val spectators: List<Any?>? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("folder")
    val folder: Int? = null,

    @field:SerializedName("files")
    val files: List<Any?>? = null,

    @field:SerializedName("functional_group")
    val functionalGroup: List<Any?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("participants")
    val participants: List<Any?>? = null
)

data class Leader(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null
)

data class Performer(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null
)
