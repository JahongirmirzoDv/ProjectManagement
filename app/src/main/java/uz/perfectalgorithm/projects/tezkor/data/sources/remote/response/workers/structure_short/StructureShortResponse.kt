package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import java.io.Serializable

/**
 *Created by farrukh_kh on 10/31/21 10:18 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short
 **/
data class StructureShortResponse(
    @field:SerializedName("data")
    val data: DataStructureShort? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

@Parcelize
data class DataStructureShort(
    @field:SerializedName("data_list")
    val structureList: List<DepartmentStructureShort?>? = null,

//    @field:SerializedName("owners")
//    val owners: @RawValue List<Any?>? = null,
) : Parcelable

@Parcelize
data class DepartmentStructureShort(
    @field:SerializedName("parent")
    val parent: @RawValue Any? = null,

    @field:SerializedName("children")
    val children: List<DepartmentStructureShort>? = null,

    @field:SerializedName("positions")
    val positions: List<PositionStructureShort?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
) : Parcelable

@Parcelize
data class PositionStructureShort(
    @field:SerializedName("hierarchy")
    val hierarchy: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("staffs")
    val staffs: List<AllWorkersShort>? = null
) : Parcelable

@Parcelize
data class WorkerStructureShort(
    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("full_name")
    val fullName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("position")
    val position: List<PositionStructureShort>? = null,

    @Expose
    var isChecked: Boolean = false,

    @Expose
    var viewType: Int = 1
) : Serializable, Parcelable {

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
            position
        )
    }
}
