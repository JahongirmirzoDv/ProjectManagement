package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse

sealed class GroupChatDetailResponse {
    data class ResponseData(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("members")
        val members: List<AllWorkersShortDataResponse.WorkerShortDataItem>,

        @field:SerializedName("project")
        val project: Any? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    )

}
