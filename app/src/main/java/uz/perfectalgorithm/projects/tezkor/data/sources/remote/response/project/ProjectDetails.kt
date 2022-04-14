package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskCommentData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.PositionStructureShort
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.BaseDetails


data class ProjectDetails(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("description")
    override val description: String,
    @field:SerializedName("yaqm")
    override val yaqm: String,
    @field:SerializedName("status")
    override val status: StatusData,
    @field:SerializedName("internal_status")
    val internalStatus: String?,
    @field:SerializedName("comments")
//    val comments: ArrayList<CauseCommentsResponse>?,
    val comments: ArrayList<TaskCommentData>?,
    @field:SerializedName("importance")
    override val importance: String,
    @field:SerializedName("is_pinned")
    val isPinned: Boolean,
    @field:SerializedName("start_time")
    override val startTime: String,
    @field:SerializedName("end_time")
    override val endTime: String,
    @field:SerializedName("performer")
    override val performer: PersonData,
    @field:SerializedName("leader")
    override val leader: PersonData,
    @field:SerializedName("files")
    override val files: List<File>?,
    @field:SerializedName("goal")
    val goal: Goal,
    @field:SerializedName("participants")
    override val participants: List<PersonData>,
    @field:SerializedName("spectators")
    override val spectators: List<PersonData>,
    @field:SerializedName("functional_group")
    override val functionalGroup: List<PersonData>,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("last_updated")
    val lastUpdated: String,
    @field:SerializedName("creator")
    override val creatorObject: PersonData?,
    @field:SerializedName("is_editable")
    override val canEditTime: Boolean?,
    @field:SerializedName("projects_plans")
    val plans: List<DiscussedTopic>?,
    @field:SerializedName("repeat_rule")
    override val repeatRule: Int,
    @field:SerializedName("repeat")
    override val repeat: String?,
    @field:SerializedName("until_date")
    override val reminderDate: String?
) : BaseDetails {

    @SerializedName("none")
    override val members: List<MeetingMember>? = null

    @SerializedName("none_none")
    override val creator: Int? = null
}

@Parcelize
data class PersonData(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("full_name")
    val fullName: String,
    @field:SerializedName("image")
    val image: String?,
    @Expose
    val position: List<PositionStructureShort?>? = null,
    @field:SerializedName("leader")
    val leader: PersonData? = null,
    var isCheckedMember: Boolean = false
) : Parcelable

data class File(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("file")
    val link: String,
    @field:SerializedName("size")
    val size: Int,
)

data class Goal(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
)