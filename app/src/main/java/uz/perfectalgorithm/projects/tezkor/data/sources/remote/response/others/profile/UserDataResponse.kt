package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile

import com.google.gson.annotations.SerializedName

sealed class UserDataResponse {

    data class Result(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class Department(

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )

    data class Data(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("get_tasks_count")
        val getTasksCount: GetTasksCount? = null,

        @field:SerializedName("reports")
        val reports: List<Any?>? = null,

        @field:SerializedName("sex")
        val sex: String? = null,

        @field:SerializedName("birth_date")
        val birthDate: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("phone_number")
        val phoneNumber: String? = null,

        @field:SerializedName("positions")
        val positions: List<PositionsItem?>? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("leader")
        val leader: List<LeaderDataItem>? = null,

        @field:SerializedName("is_outsource")
        val isOutsource: Boolean? = null,
        )

    data class PermissionsItem(

        @field:SerializedName("title_ru")
        val titleRu: String? = null,

        @field:SerializedName("title_uz")
        val titleUz: String? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )

    data class PositionsItem(

        @field:SerializedName("permissions")
        val permissions: List<PermissionsItem?>? = null,

        @field:SerializedName("hierarchy")
        val hierarchy: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("department")
        val department: Department? = null
    )

    data class GetTasksCount(

        @field:SerializedName("all")
        val all: Int? = null,

        @field:SerializedName("done")
        val done: Int? = null,

        @field:SerializedName("undone")
        val undone: Int? = null
    )

    data class LeaderDataItem(
        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("full_name")
        val fullName: String? = null,

        @field:SerializedName("image")
        val image: String? = null,

        )

}