package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal

import com.google.gson.annotations.SerializedName

sealed class GetChatListRequest {
    data class RequestData(
        @field:SerializedName("data")
        val data: Any? = null,

        @field:SerializedName("action")
        val action: String
    )

}