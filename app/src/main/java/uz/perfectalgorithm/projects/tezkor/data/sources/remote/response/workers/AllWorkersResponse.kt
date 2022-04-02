package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.PositionStructureShort
import java.io.Serializable

sealed class AllWorkersResponse {
    data class Result(

        @field:SerializedName("data")
        val data: List<DataItem>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    ) : Serializable


    data class PositionItem(

        @field:SerializedName("hierarchy")
        val hierarchy: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("title")
        val title: String? = null
    ) : Serializable {
        fun toPositionStructureShort() = PositionStructureShort(
            hierarchy, id, title
        )
    }

    @Parcelize
    data class DataItem(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("role")
        val role: String? = null,

        @field:SerializedName("birth_date")
        val birthDate: String? = null,

        @field:SerializedName("sex")
        val sex: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("phone_number")
        val phoneNumber: String? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("boss_name")
        val bossName: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("contact")
        val contact: Contact? = null,

        @field:SerializedName("position")
        val position: List<PositionItem>? = null,

        @field:SerializedName("is_favourite")
        var isFavourite: Boolean? = null,

        @field:SerializedName("is_outsource")
        var isOutsource: Boolean? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("tasks")
        val tasks: TasksCountData? = null,

        @field:SerializedName("report_period")
        val reportPeriod: String? = null,

        @field:SerializedName("leader")
        val leader: List<LeaderDataItem>? = null,

        @Expose
        var isChecked: Boolean = false,

        @Expose
        var viewType: Int = 1

    ) : Serializable, Parcelable {
        fun getFullName() = "$firstName $lastName"

        fun getPositionsTitle(positionType: HierarchyPositionsEnum): String {
            var string = ""

            position?.filter { it?.hierarchy == positionType.text }?.let { positionList ->
                positionList.forEach { position ->
                    string += position?.title
                    if (position != positionList.last()) string += "/"
                }
            }
            return string
        }

        fun toPersonData() = id?.let {
            PersonData(
                it,
                getFullName(),
                image,
                position?.map { it.toPositionStructureShort() }
            )
        }
    }

    @Parcelize
    data class LeaderDataItem(
        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("full_name")
        val fullName: String? = null,

        @field:SerializedName("image")
        val image: String? = null,

        ) : Parcelable

    data class TasksCountData(

        @field:SerializedName("all")
        val all: Int? = null,

        @field:SerializedName("done")
        val done: Int? = null,

        @field:SerializedName("undone")
        val undone: Int? = null
    ) : Serializable


    data class Contact(
        @field:SerializedName("first_name")
        val firstName: String? = null,
        @field:SerializedName("last_name")
        val lastName: String? = null,
    ) : Serializable

}
