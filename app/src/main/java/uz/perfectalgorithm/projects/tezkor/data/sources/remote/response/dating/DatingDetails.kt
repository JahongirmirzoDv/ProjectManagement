package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.File
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.UpdatedHistory
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.BaseDetails

/**
 *Created by farrukh_kh on 7/24/21 10:18 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.dating
 **/
data class DatingDetails(
    @field:SerializedName("id")
    override val id: Int,
//    @field:SerializedName("title")
//    override val title: String,
    @field:SerializedName("description")
    override val description: String,
    @field:SerializedName("start_time")
    override val startTime: String,
    @field:SerializedName("end_time")
    override val endTime: String,
    @field:SerializedName("members")
    override val participants: List<PersonData>,
    @field:SerializedName("files")
    override val files: List<File>?,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("importance")
    override val importance: String,
    @field:SerializedName("repeat")
    override val repeat: String?,
//    @field:SerializedName("reminder")
//    val reminder: Int?,
    @field:SerializedName("until_date")
    override val reminderDate: String?,
    @field:SerializedName("partner_ins")
    val partnerIn: List<PersonData>?,
    @field:SerializedName("partner_out")
    val partnerOut: String?,
    @field:SerializedName("creator")
    override val creatorObject: PersonData?,
    @field:SerializedName("reminders")
    val reminders: List<Int>,
    @field:SerializedName("repeat_rule")
    override val repeatRule: Int,
    @field:SerializedName("updates_history")
    val updatesHistory: List<UpdatedHistory>? = null,
    @field:SerializedName("status")
    val sts: String? = null
) : BaseDetails {
    override val title: String? = null
    override val yaqm: String? = null
    override val leader: PersonData? = null
    override val performer: PersonData? = null

    @SerializedName("none_none_none")
    override val status: StatusData? = null
    override val spectators: List<PersonData>? = null
    override val functionalGroup: List<PersonData>? = null
    override val canEditTime: Boolean? = null

    @SerializedName("none")
    override val members: List<MeetingMember>? = null

    @SerializedName("none_none")
    override val creator: Int? = null
}