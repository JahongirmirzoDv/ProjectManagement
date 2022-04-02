package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class RefreshTokenResponse(
    @field:SerializedName("access")
    val access: String
) : Serializable
