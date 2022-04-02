package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.File
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.BaseDetails

/**
 *Created by farrukh_kh on 8/7/21 9:20 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.goal
 **/
data class GoalDetails(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("description")
    override val description: String,
    @field:SerializedName("status")
    val statusString: String,
    @field:SerializedName("start_time")
    override val startTime: String,
    @field:SerializedName("end_time")
    override val endTime: String,
    @field:SerializedName("performer")
    override val performer: PersonData,
    @field:SerializedName("leader")
    override val leader: PersonData,
    @field:SerializedName("files")
    override val files: List<File>,
    @field:SerializedName("folder")
    val folder: Folder,
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
    override val creator: Int?,
    @field:SerializedName("is_editable")
    override val canEditTime: Boolean?
) : BaseDetails {
    override val yaqm: String? = null

    @field:SerializedName(".")
    override val status: StatusData? = null
    override val importance: String? = null
    override val reminderDate: String? = null
    override val repeat: String? = null

    @SerializedName("none")
    override val members: List<MeetingMember>? = null
    override val creatorObject: PersonData? = null
    override val repeatRule: Int? = null
}

data class Folder(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
)