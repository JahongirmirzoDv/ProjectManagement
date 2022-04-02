package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import android.content.res.Resources
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.joda.time.Days
import org.joda.time.LocalDateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem

/**
 *Created by farrukh_kh on 7/22/21 11:28 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.task
 **/
@Parcelize
data class TaskFolderListItem(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("tasks")
    val tasks: List<TasksByStatus>?
) : Parcelable

@Parcelize
data class TasksByStatus(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("is_viewable")
    val isViewable: Boolean?,
    @field:SerializedName("tasks")
    override val items: List<FolderItem>
) : DragColumn, Parcelable {
    override fun getColumnIndex() = 0

    override fun setColumnIndex(columnIndex: Int) {}

    override fun equals(other: Any?) = if (other is TasksByStatus) {
        id == other.id &&
                title == other.title &&
                items.containsAll(other.items) &&
                other.items.containsAll(items)
    } else {
        super.equals(other)
    }
}

@Parcelize
data class FolderItem(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("performer")
    val performer: String?,
    @field:SerializedName("start_time")
    val startTime: String?,
    @field:SerializedName("end_time")
    val endTime: String?,
    @field:SerializedName("status")
    val status: Status?,
    @field:SerializedName("internal_status")
    val internal_status: String?,
    @field:SerializedName("importance")
    val importance: String?,
    @field:SerializedName("is_pinned")
    val isPinned: Boolean?,
    @field:SerializedName("files_count")
    val filesCount: Int?,
    @field:SerializedName("messages_count")
    val messagesCount: Int?,
    @field:SerializedName("performer_id")
    val performerId: Int?,
    @field:SerializedName("creator_id")
    val creatorId: Int?,
) : DragItem, Parcelable {
    override fun getColumnIndex() = 0

    override fun getItemIndex() = 0

    override fun setColumnIndex(columnIndex: Int) {}

    override fun setItemIndex(itemIndex: Int) {}

    fun getDeadlineColor(resources: Resources) = if (endTime.isNullOrBlank()) {
        null
    } else {
        val endDateTime = LocalDateTime.parse(endTime)
        val days = Days.daysBetween(LocalDateTime(), endDateTime).days

        when {
            days > 4 -> Pair(
                ResourcesCompat.getColor(resources, R.color.deadline_green, null),
                ResourcesCompat.getColor(resources, R.color.deadline_green_bg, null)
            )
            days > 1 -> Pair(
                ResourcesCompat.getColor(resources, R.color.deadline_yellow, null),
                ResourcesCompat.getColor(resources, R.color.deadline_yellow_bg, null)
            )
            else -> Pair(
                ResourcesCompat.getColor(resources, R.color.deadline_red, null),
                ResourcesCompat.getColor(resources, R.color.deadline_red_bg, null)
            )
        }
    }
}