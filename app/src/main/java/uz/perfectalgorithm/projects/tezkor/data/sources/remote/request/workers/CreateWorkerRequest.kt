package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

data class CreateWorkerRequest(
    val phone_number: String,
    val position: List<Int>,
    val first_name: String,
    val last_name: String,
    val email: String,
    val birth_date: String,
    val password: String,
    val sex: String,
    val is_outsource: Boolean,
)