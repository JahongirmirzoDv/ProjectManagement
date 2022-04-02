package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group

import com.google.gson.annotations.SerializedName

sealed class GroupChatMessageListResponse {
    data class ResponseData(

        @field:SerializedName("next")
        val next: String? = null,

        @field:SerializedName("previous")
        val previous: String? = null,

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("pages_count")
        val pagesCount: Int? = null,

        @field:SerializedName("results")
        val results: List<GroupChatSendMessageResponse.MessageDataItem>
    )
}