package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_plan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlanDay
import uz.perfectalgorithm.projects.tezkor.databinding.ItemQuickPlanDayBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.translateWeeks

/**
 *Created by farrukh_kh on 8/18/21 12:43 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.quick_plan
 **/

class QuickPlanDayAdapter(
    private val onAddChildQuickPlan: SingleBlock<CreateQuickPlanRequest>,
    private val onChangeStatus: DoubleBlock<Int, Boolean>,
    private val onEditClick: SingleBlock<Int>,
    private val onVisibleMainETListener: SingleBlock<Boolean>,
    private val onDropItemListener: DoubleBlock<String, String>,
    private val context: Context
    ) : ListAdapter<QuickPlanDay, QuickPlanDayAdapter.VH>(ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemQuickPlanDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }


    inner class VH(private val binding: ItemQuickPlanDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val quickPlanAdapter by lazy {
            QuickPlanAdapter(
                onAddChildQuickPlan,
                onChangeStatus,
                onEditClick,
                onVisibleMainETListener,
                onDropItemListener
            )
        }

        init {
            binding.rvQuickPlans.adapter = quickPlanAdapter
        }

        fun bind(quickPlanDay: QuickPlanDay) = with(binding) {
            tvWeekDay.text = quickPlanDay.weekday.translateWeeks(context)
            tvDay.text = quickPlanDay.date
            tvEmpty.isVisible = quickPlanDay.quickPlans.isEmpty()
            quickPlanAdapter.submitList(quickPlanDay.quickPlans)
        }


    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<QuickPlanDay>() {
            override fun areItemsTheSame(oldItem: QuickPlanDay, newItem: QuickPlanDay) =
                oldItem.weekday == newItem.weekday

            override fun areContentsTheSame(oldItem: QuickPlanDay, newItem: QuickPlanDay) =
                oldItem.date == newItem.date &&
                        oldItem.quickPlans.size == newItem.quickPlans.size &&
                        oldItem.quickPlans.containsAll(newItem.quickPlans) &&
                        newItem.quickPlans.containsAll(oldItem.quickPlans)

            override fun getChangePayload(oldItem: QuickPlanDay, newItem: QuickPlanDay) = false
        }
    }
}