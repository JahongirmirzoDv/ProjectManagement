package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group

import com.google.gson.annotations.SerializedName


sealed class UploadGroupImageFileResponse {
    data class ResponseData(

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
        @field:SerializedName("image")
        val image: String? = null
    )

}