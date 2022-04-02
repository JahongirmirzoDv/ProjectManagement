package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal

import android.net.Uri
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse

sealed class PersonalChatMessageListResponse {
    data class ResponseData(

        @field:SerializedName("next")
        val next: String? = null,

        @field:SerializedName("previous")
        val previous: String? = null,

        @field:SerializedName("pages_count")
        val pagesCount: Int? = null,

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("results")
        val results: List<MessageDataItem>
    )

    data class MessageDataItem(

        @field:SerializedName("is_read")
        val isRead: Boolean? = null,

        @field:SerializedName("creator")
        val creator: Creator? = null,

        @field:SerializedName("task")
        val task: ShortTaskData? = null,

        @field:SerializedName("meeting")
        val meeting: ShortMeetingData? = null,

        @field:SerializedName("note")
        val note: ShortNoteData? = null,

        @field:SerializedName("time_created")
        val timeCreated: String? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("chat_id")
        val chatId: Int? = null,

        @field:SerializedName("files")
        val files: List<FilesItem>,

        @field:SerializedName("message_type")
        val messageType: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("answer_for")
        val answerFor: AnswerFor? = null,

        @Expose
        val localImgUri: Uri? = null

        )

    data class AnswerFor(

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("creator")
        val creator: Creator? = null,

        @field:SerializedName("files")
        val files: List<FilesItem>? = null,

        )

    data class FilesItem(

        @field:SerializedName("file")
        val file: String,

        @field:SerializedName("time_created")
        val timeCreated: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("time_updated")
        val timeUpdated: String? = null
    )

    data class ShortTaskData(
        @field:SerializedName("id")
        val id: Int? = null,
        @field:SerializedName("title")
        val title: String? = null,
        @field:SerializedName("status")
        val status: String? = null,
        @field:SerializedName("leader_image")
        val leaderImage: String? = null,
        @field:SerializedName("performer_image")
        val performerImage: String? = null,
    )

    data class ShortMeetingData(
        @field:SerializedName("id")
        val id: Int? = null,
        @field:SerializedName("title")
        val title: String? = null,
        @field:SerializedName("status")
        val status: String? = null,
    )

    data class ShortNoteData(
        @field:SerializedName("id")
        val id: Int? = null,
        @field:SerializedName("title")
        val title: String? = null,
        @field:SerializedName("is_active")
        val status: Boolean? = null,

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
