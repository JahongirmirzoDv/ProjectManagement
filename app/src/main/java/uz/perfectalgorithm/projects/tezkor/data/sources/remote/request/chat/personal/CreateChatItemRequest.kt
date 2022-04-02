package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal

import com.google.gson.annotations.SerializedName

sealed class CreateChatItemRequest {
    data class RequestData(
        @field:SerializedName("data")
        val data: CreateData,

        @field:SerializedName("action")
        val action: String
    )

    data class CreateData(

        @field:SerializedName("sender")
        val sender: Int,

        @field:SerializedName("receiver")
        val receiver: Int
    )

}
