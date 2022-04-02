package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth

import com.google.gson.annotations.SerializedName

data class TokenRequest(

    @field:SerializedName("password")
    val password: String? = null,
    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,
    @field:SerializedName("registration_id")
    var firebaseToken: String? = null,
)