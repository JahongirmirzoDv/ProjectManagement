package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment

import com.google.gson.annotations.SerializedName

/**
 * Created by Jasurbek Kurganbaev on 04.08.2021 14:09
 **/
data class CommentListBody(
    @field:SerializedName("id")
    val id: Int? = null,
)