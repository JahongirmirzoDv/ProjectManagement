package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure

import com.google.gson.annotations.SerializedName


sealed class StructurePositionsResponse {

    data class Result(

        @field:SerializedName("data")
        val data: List<DataItem?>? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class DataItem(

        @field:SerializedName("parent")
        val parent: Any? = null,

        @field:SerializedName("children")
        val children: List<DataItem?>? = null,

        @field:SerializedName("positions")
        val positions: List<PositionsItem?>? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )

    data class PositionsItem(

        @field:SerializedName("hierarchy")
        val hierarchy: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )
}
