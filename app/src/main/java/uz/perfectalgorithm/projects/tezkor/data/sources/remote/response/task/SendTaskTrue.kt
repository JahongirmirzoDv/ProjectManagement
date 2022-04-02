package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

data class SendTaskTrue(
    val date: String,
    val id: Int,
    val is_done: Boolean,
    val parent: Any,
    val task_status: Boolean,
    val title: String
)