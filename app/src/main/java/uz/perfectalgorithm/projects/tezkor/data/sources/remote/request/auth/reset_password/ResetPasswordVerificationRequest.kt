package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Created by Jasurbek Kurganbaev on 9/20/2021 7:04 AM
 **/
@Parcelize
data class ResetPasswordVerificationRequest(
    @field:SerializedName("phone_number")
    val phoneNumber: String,
    @field:SerializedName("password")
    val password: String,
    @field:SerializedName("code")
    var code: String
) : Parcelable