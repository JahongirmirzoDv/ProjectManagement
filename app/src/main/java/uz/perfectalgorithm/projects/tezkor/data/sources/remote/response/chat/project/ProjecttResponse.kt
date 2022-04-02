package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project

import com.google.gson.annotations.SerializedName

data class ProjectResponse(

    @field:SerializedName("data")
    val data: List<ProjectData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class PositionItem(

    @field:SerializedName("permissions")
    val permissions: List<Any?>? = null,

    @field:SerializedName("hierarchy")
    val hierarchy: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("department")
    val department: Department? = null
)

data class Creator(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("reports")
    val reports: List<Any?>? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("report_period")
    val reportPeriod: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,

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

data class Department(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)

data class FunctionalGroupItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("reports")
    val reports: List<Any?>? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("report_period")
    val reportPeriod: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,

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

data class PerformerId(

    @field:SerializedName("image")
    val image: String? = null,

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

data class SpectatorsItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("reports")
    val reports: List<Any?>? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("report_period")
    val reportPeriod: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,

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

data class Status(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)

data class ParticipantsItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("reports")
    val reports: List<Any?>? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("report_period")
    val reportPeriod: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,

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

data class Leader(

    @field:SerializedName("image")
    val image: String? = null,

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

/*data class ProjectData(

    @field:SerializedName("leader")
    val leader: Leader? = null,

    @field:SerializedName("creator")
    val creator: Creator? = null,

    @field:SerializedName("last_updated")
    val lastUpdated: String? = null,

    @field:SerializedName("goal")
    val goal: Any? = null,

    @field:SerializedName("importance")
    val importance: String? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("spectators")
    val spectators: List<SpectatorsItem?>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("real_duration")
    val realDuration: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("is_pinned")
    val isPinned: Boolean? = null,

    @field:SerializedName("duration")
    val duration: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("performer_id")
    val performerId: PerformerId? = null,

    @field:SerializedName("functional_group")
    val functionalGroup: List<FunctionalGroupItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("yaqm")
    val yaqm: String? = null,

    @field:SerializedName("participants")
    val participants: List<Any?>? = null,

    @field:SerializedName("status")
    val status: Status? = null
)*/

data class Tasks(

    @field:SerializedName("all")
    val all: Int? = null,

    @field:SerializedName("done")
    val done: Int? = null,

    @field:SerializedName("undone")
    val undone: Int? = null
)
