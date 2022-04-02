package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat

import com.google.gson.annotations.SerializedName

sealed class AddMembersToGroupChatRequest {

    data class RequestData(
        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(

        @field:SerializedName("chat_id")
        val chatId: Int? = null,

        @field:SerializedName("members_list")
        val membersList: List<Int>? = null
    )
}
