package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.PositionStructureShort


data class AllWorkersShortResponse(
    @field:SerializedName("data")
    val data: List<AllWorkersShort>,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

@Parcelize
data class AllWorkersShort(
    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("full_name")
    val fullName: String? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("is_favourite")
    var isFavourite: Boolean? = null,

    @field:SerializedName("position")
    val position: List<PositionStructureShort>? = null,

    @field:SerializedName("leader")
    val leader: PersonData? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @Expose
    var isChecked: Boolean = false,

    @Expose
    var viewType: Int = 1
) : Parcelable {
    fun getPositionsTitle(positionType: HierarchyPositionsEnum): String {
        var string = ""

        position?.filter { it.hierarchy == positionType.text }?.let { positionList ->
            positionList.forEach { position ->
                string += position.title
                if (position != positionList.last()) string += "/"
            }
        }
        return string
    }

    fun toPersonData() = id?.let {
        PersonData(
            it,
            fullName ?: "",
            image,
            position,
            leader
        )
    }
}
