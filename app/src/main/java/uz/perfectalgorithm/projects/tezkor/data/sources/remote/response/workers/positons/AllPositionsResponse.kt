package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.positons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.PositionsStateEnum
import java.io.Serializable

sealed class AllPositionsResponse {
    data class Result(

        @field:SerializedName("data")
        val data: List<PositionDataItem>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    ) : Serializable

    data class PositionDataItem(

        @field:SerializedName("permissions")
        val permissions: List<Int?>? = null,

        @field:SerializedName("hierarchy")
        val hierarchy: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("department")
        val department: Int? = null,

        @Expose
        var isOld: Boolean = false,

        @Expose
        var state: String = PositionsStateEnum.DELETED.text,

        ) : Serializable
}
