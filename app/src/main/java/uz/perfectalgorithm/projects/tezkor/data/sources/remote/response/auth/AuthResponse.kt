package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class AuthResponse {

    data class TokenResponse(
        @field:SerializedName("access")
        val access: String,

        @field:SerializedName("refresh")
        val refresh: String,

        @field:SerializedName("user")
        val user: UserData,

        ) : Serializable

    data class UserData(

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("phohe_number")
        val phoneNumber: String? = null,

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("role")
        val role: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("email")
        val email: String? = null
    ) : Serializable


}