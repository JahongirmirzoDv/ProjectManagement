package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse

sealed class ChatGroupListResponse {

    data class Result(

        @field:SerializedName("data")
        val data: List<GroupChatDataItem>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class GroupChatDataItem(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("project")
        val project: Any? = null,

        @field:SerializedName("unread_messages")
        val unreadMessages: Int? = null,

        @field:SerializedName("last_message")
        val lastMessage: LastMessage? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("members_count")
        val membersCount: Int? = null,
        @field:SerializedName("is_channel")
        val isChannel: Boolean? = false
    )

    data class LastMessage(

        @field:SerializedName("is_read")
        val isRead: Boolean? = null,

        @field:SerializedName("creator")
        val creator: PersonalChatMessageListResponse.Creator? = null,

        @field:SerializedName("task")
        val task: PersonalChatMessageListResponse.ShortTaskData? = null,

        @field:SerializedName("time_created")
        val timeCreated: String? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("chat_id")
        val chatId: Int? = null,

        @field:SerializedName("files")
        val files: List<PersonalChatMessageListResponse.FilesItem>,

        @field:SerializedName("message_type")
        val messageType: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,
    )

    data class Creator(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("full_name")
        val fullName: String? = null,

        @field:SerializedName("id")
        val id: Int? = null
    )

}