package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel

import com.google.gson.annotations.SerializedName

data class ChannelResponse(

    @field:SerializedName("data")
    val data: List<ChannelData>,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class ChannelData(

    @field:SerializedName("image")
    val image: Any? = null,

    @field:SerializedName("creator")
    val creator: Int? = null,

    @field:SerializedName("members")
    val members: List<Int?>? = null,

    @field:SerializedName("admin")
    val admin: List<Int?>? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)
