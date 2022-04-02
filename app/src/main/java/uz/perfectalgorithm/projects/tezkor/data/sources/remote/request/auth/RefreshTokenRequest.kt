package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @field:SerializedName("refresh")
    val refresh: String? = null
)
