package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth

import com.google.gson.annotations.SerializedName

data class UpdateUserDataRequest(
    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,
)