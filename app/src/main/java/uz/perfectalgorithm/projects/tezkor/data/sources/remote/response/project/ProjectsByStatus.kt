package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project

import android.content.res.Resources
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import org.joda.time.Days
import org.joda.time.LocalDateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem

/**
 *Created by farrukh_kh on 8/5/21 9:57 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.project
 **/
data class ProjectsByStatus(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("projects")
    override val items: List<Project>
) : DragColumn {
    override fun getColumnIndex() = 0

    override fun setColumnIndex(columnIndex: Int) {}

    override fun equals(other: Any?) = if (other is ProjectsByStatus) {
        id == other.id &&
                title == other.title &&
                items.containsAll(other.items) &&
                other.items.containsAll(items)
    } else {
        super.equals(other)
    }
}

data class Project(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
//    @field:SerializedName("description")
//    val description: Int,
    @field:SerializedName("status")
    val status: StatusData?,
    @field:SerializedName("importance")
    val importance: String,
    @field:SerializedName("date")
    val endTime: String,
    @field:SerializedName("leader")
    val leader: String,
    @field:SerializedName("performer_id")
    val performerId: Int?,
    @field:SerializedName("creator_id")
    val creatorId: Int?,
) : DragItem {
    override fun getColumnIndex() = 0

    override fun getItemIndex() = 0

    override fun setColumnIndex(columnIndex: Int) {}

    override fun setItemIndex(itemIndex: Int) {}

    fun getDeadlineColor(resources: Resources): Pair<Int, Int> {
        val endDateTime = LocalDateTime.parse(endTime)
        val days = Days.daysBetween(LocalDateTime(), endDateTime).days
        Log.e("deays", "title=$title days=$days")

        return when {
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