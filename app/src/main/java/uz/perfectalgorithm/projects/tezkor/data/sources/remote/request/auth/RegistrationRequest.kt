package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @field:SerializedName("phone_number")
    val phoneNumber: String
)