package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting

import com.google.gson.annotations.SerializedName

class MeetingComment(
    val id:Int,
    val meeting:Int,
    val text:String,
    val commenter:String,
    val avatar:String?,
    @SerializedName("replied_comments")
    val repliedComment:List<MeetingComment>,
    @SerializedName("created_at")
    val createdAt:String
)