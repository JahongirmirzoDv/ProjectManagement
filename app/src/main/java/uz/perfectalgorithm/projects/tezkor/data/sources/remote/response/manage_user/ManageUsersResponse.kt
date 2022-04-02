package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user

import com.google.gson.annotations.SerializedName

data class ManageUsersResponse(
    @field:SerializedName("data")
    val users: List<ManageUsers>,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("success")
    val success: Boolean
)

data class ManageUsers(
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("last_name")
    val lastName: String
)