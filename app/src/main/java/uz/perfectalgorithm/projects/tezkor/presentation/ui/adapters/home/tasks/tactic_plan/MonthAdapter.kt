package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.tactic_plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanMonth
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanStatus
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTacticPlanMonthBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.translateMonth


class MonthAdapter(
    private val onTacticPlanClick: SingleBlock<Int>,
    private val onAddTacticPlanClick: (Int, Int, Status) -> Unit,
    private val changeItemStatus: (TacticPlan, TacticPlanStatus) -> Unit
) : ListAdapter<TacticPlanMonth, MonthAdapter.VH>(ITEM_CALLBACK) {

    var yearId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemTacticPlanMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemTacticPlanMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val statusAdapter by lazy {
            StatusAdapter(
                binding.root.context,
                onTacticPlanClick,
                onAddTacticPlanClick,
                changeItemStatus
            )
        }

        init {
            binding.dragBoardView.setHorizontalAdapter(statusAdapter)
        }

        fun bind(month: TacticPlanMonth) = with(binding) {
            tvTitle.text = month.title.translateMonth(this.root.context)
            statusAdapter.yearId = yearId
            statusAdapter.monthId = month.id
            statusAdapter.submitList(month.statuses)
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<TacticPlanMonth>() {
            override fun areItemsTheSame(
                oldItem: TacticPlanMonth,
                newItem: TacticPlanMonth
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TacticPlanMonth,
                newItem: TacticPlanMonth
            ) = oldItem.statuses.toTypedArray().contentDeepEquals(newItem.statuses.toTypedArray())

            override fun getChangePayload(
                oldItem: TacticPlanMonth,
                newItem: TacticPlanMonth
            ) = false
        }
    }
}