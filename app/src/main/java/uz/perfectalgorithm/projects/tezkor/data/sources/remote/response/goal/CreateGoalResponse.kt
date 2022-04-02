package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal

import com.google.gson.annotations.SerializedName

data class CreateGoalResponse(

    @field:SerializedName("data")
    val data: CreateGoalData,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class CreateGoalData(

    @field:SerializedName("leader")
    val leader: Int? = null,

    @field:SerializedName("creator")
    val creator: Int? = null,

    @field:SerializedName("last_updated")
    val lastUpdated: String? = null,

    @field:SerializedName("performer")
    val performer: Int? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("spectators")
    val spectators: List<Int?>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("folder")
    val folder: Int? = null,

    @field:SerializedName("get_files")
    val getFiles: List<Any?>? = null,

    @field:SerializedName("functional_group")
    val functionalGroup: List<Int?>? = null,

    @field:SerializedName("files")
    val files: List<FilesItem?>? = null,

    @field:SerializedName("company")
    val company: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("participants")
    val participants: List<Int?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class FilesItem(

    @field:SerializedName("file")
    val file: String? = null,

    @field:SerializedName("size")
    val size: Int? = null
)
