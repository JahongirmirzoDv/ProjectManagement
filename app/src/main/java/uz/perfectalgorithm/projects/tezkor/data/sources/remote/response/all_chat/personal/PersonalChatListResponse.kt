package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

sealed class PersonalChatListResponse {
    data class Result(

        @field:SerializedName("data")
        val data: List<PersonalChatDataItem>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class PersonalChatDataItem(

        @field:SerializedName("last_message")
        val lastMessage: PersonalChatMessageListResponse.MessageDataItem? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("sender_last_seen")
        val senderLastSeen: Any? = null,

        @field:SerializedName("unread_messages_count")
        val unreadMessagesCount: Int? = null,

        @field:SerializedName("receiver")
        val receiver: PersonalChatMessageListResponse.Creator? = null,


        @Expose
        var isOpen: Boolean = false
    )

}