package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

import com.google.gson.annotations.SerializedName

data class RemoveUserFromFavouritesRequest(
    @field:SerializedName("user_id")
    val UserId: Int
)
