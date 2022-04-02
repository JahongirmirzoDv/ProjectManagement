package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages

import com.google.gson.annotations.SerializedName

sealed class UpdateTaskMessageResponse {

    data class Result(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class Data(

        @field:SerializedName("leader")
        val leader: Int? = null,

        @field:SerializedName("creator")
        val creator: Int? = null,

        @field:SerializedName("is_done")
        val isDone: Boolean? = null,

        @field:SerializedName("performer")
        val performer: Int? = null,

        @field:SerializedName("importance")
        val importance: String? = null,

        @field:SerializedName("end_time")
        val endTime: String? = null,

        @field:SerializedName("project")
        val project: Int? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("start_time")
        val startTime: String? = null,

        @field:SerializedName("folder")
        val folder: Int? = null,

        @field:SerializedName("repeat")
        val repeat: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("yaqm")
        val yaqm: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
    )
}
