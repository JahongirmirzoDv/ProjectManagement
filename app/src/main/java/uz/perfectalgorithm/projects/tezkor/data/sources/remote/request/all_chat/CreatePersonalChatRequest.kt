package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat

import com.google.gson.annotations.SerializedName

sealed class CreatePersonalChatRequest {
    data class RequestData(
        @field:SerializedName("data")
        val data: CreateData,

        @field:SerializedName("action")
        val action: String
    )

    data class CreateData(
        @field:SerializedName("receiver")
        val receiver: Int
    )

}
