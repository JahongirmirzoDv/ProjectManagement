package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

import com.google.gson.annotations.SerializedName

data class CreateDepartmentRequest(
    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("parent")
    val parent: Int? = null,
)
