package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

import com.google.gson.annotations.SerializedName

data class UpdateContactDataRequest(
    @field:SerializedName("user")
    val workerId: Int,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,
)