package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse

/**
 *Created by farrukh_kh on 9/30/21 5:14 PM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard
 **/
@Parcelize
data class DepartmentStructureBelow(
    @SerializedName("department_id")
    val id: Int,
    @SerializedName("department_title")
    val title: String,
    @SerializedName("positions")
    val positions: List<PositionStructureBelow>?,
    @SerializedName("children")
    val children: List<DepartmentStructureBelow>?,
) : Parcelable

@Parcelize
data class PositionStructureBelow(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("hierarchy")
    val hierarchy: String,
    @SerializedName("staffs")
    val staffs: List<AllWorkersResponse.DataItem>?,
) : Parcelable

