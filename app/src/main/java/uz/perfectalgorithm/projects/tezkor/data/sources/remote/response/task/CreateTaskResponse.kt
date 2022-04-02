package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse

data class CreateTaskResponse(

    @field:SerializedName("data")
    val data: TaskData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class TaskData(

    @field:SerializedName("parent")
    val parent: Int? = null,

    @field:SerializedName("leader")
    val leader: Int? = null,

    @field:SerializedName("creator")
    val creator: Int? = null,

    @field:SerializedName("importance")
    val importance: String? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("spectators")
    val spectators: List<Int>? = null,

    @field:SerializedName("project")
    val project: Int? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("duration")
    val duration: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("folder")
    val folder: Int? = null,

    @field:SerializedName("executor")
    val performer: Int? = null,

    @field:SerializedName("repeat")
    val repeat: String? = null,

    @field:SerializedName("functional_group")
    val functionalGroup: List<Int>? = null,

    @field:SerializedName("files")
    val files: List<BaseOffersItemResponse.GetFile>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("yaqm")
    val yaqm: String? = null,

    @field:SerializedName("participants")
    val participants: List<Int>? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
