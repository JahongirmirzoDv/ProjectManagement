package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

import com.google.gson.annotations.SerializedName

data class CreatePositionRequest(
    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("department")
    val department: Int? = null,

    @field:SerializedName("company")
    val company: Int? = null,

    @field:SerializedName("hierarchy")
    val hierarchy: String? = null,

    @field:SerializedName("permissions")
    val permissions: List<Int>? = null,

    )
