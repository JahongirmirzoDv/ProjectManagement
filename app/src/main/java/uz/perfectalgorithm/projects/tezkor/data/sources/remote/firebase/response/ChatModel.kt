package uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response

import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse

data class ChatModel(
    val id: String = "",
    val receiver: AllWorkersResponse.DataItem? = null,
    val messageModel: MessageModel? = null
)
