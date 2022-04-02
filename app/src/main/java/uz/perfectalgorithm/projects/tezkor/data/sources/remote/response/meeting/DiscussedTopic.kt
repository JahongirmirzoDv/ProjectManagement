package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.Project

/**
 *Created by farrukh_kh on 10/18/21 1:54 PM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting
 **/
data class DiscussedTopic(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("text")
    val title: String,
    val project:ProjectByTopic?=null,
    val task:ProjectByTopic?=null
)

data class ProjectByTopic(
    val id:Int,
    val creator:UserDataByTopic?,
    val performer:UserDataByTopic?
)
data class UserDataByTopic(
    val id:Int,
    @field:SerializedName("full_name")
    val fullName:String,
    val image:String
)