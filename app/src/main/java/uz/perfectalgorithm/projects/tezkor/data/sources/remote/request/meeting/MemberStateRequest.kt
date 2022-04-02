package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/31/21 10:03 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.meeting
 **/
data class MemberStateRequest(
    @SerializedName("state")
    val state: String,
    @SerializedName("description")
    val description: String? = null
)
