package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingMember
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.File
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.edit_files.EditFileAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.BaseDetails
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.FilesItem
import uz.perfectalgorithm.projects.tezkor.utils.BASE_URL
import uz.perfectalgorithm.projects.tezkor.utils.download.isImageFile

/**
 *Created by farrukh_kh on 8/6/21 10:04 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.task
 **/
data class TaskDetails(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("description")
    override val description: String?,
    @field:SerializedName("yaqm")
    override val yaqm: String?,
    @field:SerializedName("status")
    override val status: StatusData?,
    @field:SerializedName("internal_status")
    var internalStatus: String?,
    @field:SerializedName("comments")
    val comments: ArrayList<CauseCommentsResponse>?,
    @field:SerializedName("importance")
    override val importance: String?,
    @field:SerializedName("is_pinned")
    val isPinned: Boolean?,
    @field:SerializedName("is_done")
    val isDone: Boolean?,
    @field:SerializedName("start_time")
    override val startTime: String?,
    @field:SerializedName("end_time")
    override val endTime: String?,
    @field:SerializedName("folder")
    val folder: FolderData?,
    @field:SerializedName("parent")
    val parent: ParentData?,
    @field:SerializedName("repeat")
    override val repeat: String?,
    @field:SerializedName("performer")
    override val performer: PersonData?,
    @field:SerializedName("leader")
    override val leader: PersonData?,
    @field:SerializedName("files")
    override val files: List<File>?,
    @field:SerializedName("participants")
    override val participants: List<PersonData>?,
    @field:SerializedName("spectators")
    override val spectators: List<PersonData>?,
    @field:SerializedName("functional_group")
    override val functionalGroup: List<PersonData>?,
    @field:SerializedName("created_at")
    val createdAt: String?,
    @field:SerializedName("last_updated")
    val lastUpdated: String?,
    @field:SerializedName("creator")
    override val creator: Int?,
    @field:SerializedName("is_editable")
    override val canEditTime: Boolean?,
    @field:SerializedName("commentaries")
    val commentaries: ArrayList<TaskCommentData>?,
    @field:SerializedName("repeat_rule")
    override val repeatRule: Int,
    @field:SerializedName("until_date")
    override val reminderDate: String?,
    @field:SerializedName("is_calendar")
    val isShowCalendar: Boolean = true,
    @field:SerializedName("updates_history")
    val updatesHistory: List<UpdatedHistory>? = null
) : BaseDetails {

    @SerializedName("none")
    override val members: List<MeetingMember>? = null
    override val creatorObject: PersonData? = null
}

data class ParentData(
    @field:SerializedName("type")
    val type: String,
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
)

data class FolderData(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
)

fun List<File>.toListForAdapter() = map { file ->
    if (file.link.endsWith("jpeg") ||
        file.link.endsWith("jpg") ||
        file.link.endsWith("png") ||
        file.link.endsWith("svg") ||
        file.link.endsWith("bmp")
    ) {
        FilesItem(
            indexOf(file),
            file.link,
            file.link,
            type = "",
            viewType = EditFileAdapter.VIEW_TYPE_IMAGE,
            file.size.toLong(),
            remoteId = file.id
        )
    } else {
        FilesItem(
            indexOf(file),
            file.link,
            BASE_URL + file.link,
            type = "",
            viewType = EditFileAdapter.VIEW_TYPE_OTHER,
            file.size.toLong(),
            remoteId = file.id
        )
    }
}

fun List<java.io.File>.toFilesItemList() = mapIndexed { index, file ->
    FilesItem(
        id = index,
        title = file.name,
        path = file.path,
        type = "",
        viewType = if (file.name.isImageFile()) EditFileAdapter.VIEW_TYPE_IMAGE_NEW else EditFileAdapter.VIEW_TYPE_OTHER_NEW,
        size = file.length(),
        remoteId = -1,
        originalFile = file
    )
}

data class UpdatedHistory(
    val id: Int,
    val changer: String,
    @SerializedName("changer_image")
    val image: String?,
    @SerializedName("changed_date")
    val changedDate: String,
    @SerializedName("changes_array")
    val changesArray: List<String>
)