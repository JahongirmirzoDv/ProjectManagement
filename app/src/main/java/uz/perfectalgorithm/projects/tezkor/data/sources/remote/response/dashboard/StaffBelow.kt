package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 *Created by farrukh_kh on 9/22/21 10:09 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard
 **/
@Parcelize
data class StaffBelow(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("image")
    val image: String? = null
) : Parcelable {
    override fun toString() = "$firstName $lastName".trim()
}