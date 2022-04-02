package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerificationRequest(
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("last_name")
    val lastName: String,
    @field:SerializedName("phone_number")
    val phoneNumber: String,
    @field:SerializedName("password")
    val password: String,
    @field:SerializedName("code")
    var code: String
) : Parcelable