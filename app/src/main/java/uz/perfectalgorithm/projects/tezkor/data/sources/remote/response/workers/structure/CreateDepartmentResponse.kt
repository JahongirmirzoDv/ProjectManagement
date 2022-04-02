package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure

import com.google.gson.annotations.SerializedName

sealed class CreateDepartmentResponse {
    data class Result(

        @field:SerializedName("data")
        val data: DepartmentData? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class DepartmentData(

        @field:SerializedName("parent")
        val parent: Int? = null,

        @field:SerializedName("last_updated")
        val lastUpdated: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("company")
        val company: Int? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )
}