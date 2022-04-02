package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName

data class TaskFolderResponse(

    @field:SerializedName("data")
    val data: List<TaskFunctionalFolder>,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class MembersItem(

    @field:SerializedName("full_name")
    val fullName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class TaskFunctionalFolder(

    @field:SerializedName("tasks")
    val tasks: List<TaskDataItem>,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)

data class TaskDataItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("performer")
    val performer: String? = null,

    @field:SerializedName("status")
    val status: Status? = null,

    @field:SerializedName("importance")
    val importance: String? = null,

    @field:SerializedName("get_files")
    val getFiles: List<Any?>? = null,

//    @field:SerializedName("leader")
//    val leader: String? = null,
//    @field:SerializedName("real_duration")
//    val realDuration: Any? = null,
//    @field:SerializedName("created_at")
//    val createdAt: String? = null,
//    @field:SerializedName("duration")
//    val duration: String? = null,
//    @field:SerializedName("members")
//    val members: List<MembersItem?>? = null,
//    @field:SerializedName("spectator")
//    val spectator: List<SpectatorItem?>? = null,
)

data class SpectatorItem(

    @field:SerializedName("full_name")
    val fullName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
