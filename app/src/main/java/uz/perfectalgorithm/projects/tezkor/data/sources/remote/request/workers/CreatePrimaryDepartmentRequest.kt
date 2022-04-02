package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

import com.google.gson.annotations.SerializedName

data class CreatePrimaryDepartmentRequest(
    @field:SerializedName("title")
    val title: String? = null
)
