package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting

import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.File
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.BaseDetails

/**
 *Created by farrukh_kh on 7/24/21 10:15 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.meeting
 **/
data class MeetingDetails(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("description")
    override val description: String?,
    @field:SerializedName("start_time")
    override val startTime: String,
    @field:SerializedName("end_time")
    override val endTime: String,
    @field:SerializedName("members")
    override val members: List<MeetingMember>?,
    @field:SerializedName("files")
    override val files: List<File>?,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("importance")
    override val importance: String,
    @field:SerializedName("repeat")
    override val repeat: String?,
    @field:SerializedName("reminder")
    val reminder: Int?,
    @field:SerializedName("until_date")
    override val reminderDate: String?,
    @field:SerializedName("creator")
    override val creatorObject: PersonData?,
    @field:SerializedName("member_state")
    val memberState: MemberState?,
    @field:SerializedName("status")
    val meetingStatus: String?,
    @field:SerializedName("discussed_topics")
    val discussedTopics: List<DiscussedTopic>?,
    @field:SerializedName("reminders")
    val reminders: List<Int>,
    @field:SerializedName("repeat_rule")
    override val repeatRule: Int,
    @field:SerializedName("is_editable")
    override val canEditTime: Boolean?,
    val commentaries: List<MeetingComment> = listOf()
) : BaseDetails {
    @SerializedName("none")
    override val creator: Int? = null
    override val yaqm: String? = null
    override val leader: PersonData? = null
    override val performer: PersonData? = null

    @field:SerializedName("none_status")
    override val status: StatusData? = null
    override val spectators: List<PersonData>? = null
    override val functionalGroup: List<PersonData>? = null

    @SerializedName("none_none")
    override val participants: List<PersonData>? = null
}

@Parcelize
data class MeetingMember(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("user")
    val user: PersonData?,
    @SerializedName("moderator")
    val moderator: Boolean = false,
    @SerializedName("participated")
    var isCheckedMember: Boolean = false
) : Parcelable {

    fun getBgColor(): Int {
        return when (state) {
            "accepted" -> Color.parseColor("#4D06D6A0")
            "waiting" -> Color.parseColor("#4DA5A9B8")
            else -> Color.parseColor("#4DEF476F")
        }
    }

    fun getStrokeColor(): Int {
        return when (state) {
            "accepted" -> Color.parseColor("#06D6A0")
            "waiting" -> Color.parseColor("#A5A9B8")
            else -> Color.parseColor("#EF476F")
        }
    }

    fun getStateIcon(): Int {
        return when (state) {
            "accepted" -> {
                R.drawable.ic_member_accepted
            }
            "waiting" -> {
                R.drawable.ic_member_waiting
            }
            else -> {
                R.drawable.ic_member_rejected
            }
        }
    }
}

data class MemberState(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("description")
    val description: String?,
)