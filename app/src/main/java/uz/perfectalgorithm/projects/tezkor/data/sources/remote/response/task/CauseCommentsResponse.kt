package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CauseCommentsResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("last_name")
    val lastName: String,
    @field:SerializedName("comment")
    val comment: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("image")
    val creatorImage: String?
) : Parcelable