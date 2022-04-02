package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/24/21 10:09 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.meeting
 **/
data class MeetingListResponse(
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("error")
    val error: List<Any>,
    @field:SerializedName("data")
    val data: List<MeetingListItem>,
)

data class MeetingListItem(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("start_time")
    val startTime: String,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("creator")
    val creator: String,
    @field:SerializedName("members_count")
    val membersCount: Int
)