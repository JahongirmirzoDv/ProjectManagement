package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.chat

import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import java.io.Serializable

data class MembersToGroupChatData(
    val workersShortData: List<AllWorkersShortDataResponse.WorkerShortDataItem>
) : Serializable