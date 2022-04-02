package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers

import com.google.gson.annotations.SerializedName

data class EditWorkerRequest(
    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("sex")
    val gender: String? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,

    @field:SerializedName("new_positions")
    val newPositions: List<Int>? = null,

    @field:SerializedName("removed_positions")
    val removedPositions: List<Int>? = null,

    @field:SerializedName("is_outsource")
    val isOutsource: Boolean? = null,
    )