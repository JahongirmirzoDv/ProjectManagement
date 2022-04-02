package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting

data class CheckedMeeting(
    val description: Any,
    val id: Int,
    val participated: Boolean,
    val state: String
)