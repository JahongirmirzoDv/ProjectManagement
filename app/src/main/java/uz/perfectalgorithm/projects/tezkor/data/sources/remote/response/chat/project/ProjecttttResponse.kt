package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProjecttttResponse(

    @field:SerializedName("data")
    val data: List<ProjectData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

/*data class Tasks(

	@field:SerializedName("all")
	val all: Int? = null,

	@field:SerializedName("done")
	val done: Int? = null,

	@field:SerializedName("undone")
	val undone: Int? = null
)*/

data class Performer(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("leader")
    val leader: Any? = null,

    @field:SerializedName("reports")
    val reports: List<Any?>? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("report_period")
    val reportPeriod: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: Any? = null,

    @field:SerializedName("sex")
    val sex: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("position")
    val position: List<Any?>? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("tasks")
    val tasks: Tasks? = null
)

data class ProjectData(

    @field:SerializedName("performer")
    val performer: Performer? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("importance")
    var importance: String? = null
)
