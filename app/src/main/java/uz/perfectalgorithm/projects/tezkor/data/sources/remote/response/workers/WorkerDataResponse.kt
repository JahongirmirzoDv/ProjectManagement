package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName

sealed class WorkerDataResponse {

    data class Result(
        @field:SerializedName("data")
        val data: WorkerData? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class Department(

        @field:SerializedName("level")
        val level: Int? = null,

        @field:SerializedName("parent_id")
        val parentId: Any? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )

    data class WorkerData(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("role")
        val role: String? = null,

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
        val position: List<PositionData?>? = null,

        @field:SerializedName("is_favourite")
        val isFavourite: Boolean? = null,

        @field:SerializedName("is_outsource")
        val isOutsource: Boolean? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null
    )

    data class PositionData(

        @field:SerializedName("hierarchy")
        val hierarchy: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null,

        )
}