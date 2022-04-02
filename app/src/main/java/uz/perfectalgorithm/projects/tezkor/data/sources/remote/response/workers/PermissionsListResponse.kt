package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class PermissionsListResponse {

    data class Result(

        @field:SerializedName("data")
        val data: List<PermissionData>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    ) : Serializable

    data class PermissionData(

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("title")
        val title: String,

        @field:SerializedName("description")
        val description: String,

        @field:SerializedName("title_uz")
        val titleUz: String? = null,

        @field:SerializedName("title_ru")
        val titleRu: String? = null
    )

}
