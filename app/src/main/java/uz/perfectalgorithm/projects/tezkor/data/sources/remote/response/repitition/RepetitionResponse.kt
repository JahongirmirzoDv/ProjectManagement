package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition

import com.google.gson.annotations.SerializedName

data class RepetitionResponse(

    @field:SerializedName("data")
    val data: List<RepetitionData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
)

data class RepetitionData(

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("key")
    val key: String? = null
)
