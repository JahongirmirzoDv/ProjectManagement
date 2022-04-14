package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem


//data class TacticPlanResponse(
//    @field:SerializedName("id")
//    val id: Int,
//    @field:SerializedName("title")
//    val title: String
//)

data class TacticPlanYear(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("months")
    val months: List<TacticPlanMonth>
)

data class TacticPlanMonth(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("months")
    val statuses: List<TacticPlanStatus>
)

data class TacticPlanStatus(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("months")
    val tacticPlans: List<TacticPlan>
) : DragColumn {
    override val items: List<DragItem>
        get() = tacticPlans

    override fun getColumnIndex() = 0
    override fun setColumnIndex(columnIndexInHorizontalRecycleView: Int) {}

    override fun equals(other: Any?) = if (other is TacticPlanStatus) {
        id == other.id &&
                title == other.title &&
                tacticPlans.containsAll(other.tacticPlans) &&
                other.tacticPlans.containsAll(tacticPlans)
    } else {
        super.equals(other)
    }
}

data class TacticPlan(
    @field:SerializedName("id")
    override val id: Int,
    @field:SerializedName("title")
    override val title: String,
    @field:SerializedName("status")
    val status: Status,
    @field:SerializedName("date")
    val date: String,
) : DragItem {
    override fun getColumnIndex() = 0
    override fun getItemIndex() = 0
    override fun setColumnIndex(columnIndexInHorizontalRecycleView: Int) {}
    override fun setItemIndex(itemIndexInVerticalRecycleView: Int) {}

    override fun equals(other: Any?) = if (other is TacticPlan) {
        id == other.id &&
                title == other.title &&
                date == other.date &&
                status == other.status
    } else {
        super.equals(other)
    }
}

@Parcelize
data class Status(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
) : Parcelable