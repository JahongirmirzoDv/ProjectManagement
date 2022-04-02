package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DashboardGoal
import uz.perfectalgorithm.projects.tezkor.databinding.ItemDialogDashboardGoalBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

/**
 *Created by farrukh_kh on 10/8/21 9:57 AM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard
 **/
class DashboardGoalAdapter(private val onGoalSelected: SingleBlock<DashboardGoal>) :
    ListAdapter<DashboardGoal, DashboardGoalAdapter.VH>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemDialogDashboardGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemDialogDashboardGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: DashboardGoal) = with(binding) {
            root.setOnClickListener {
                onGoalSelected(goal)
            }
            if (goal.isSelected) {
                goalTitle.setTextColor(ContextCompat.getColor(root.context, R.color.blue))
            } else {
                goalTitle.setTextColor(ContextCompat.getColor(root.context, R.color.black))
            }
            goalTitle.text = goal.title.trim()
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<DashboardGoal>() {
            override fun areItemsTheSame(
                oldItem: DashboardGoal,
                newItem: DashboardGoal
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DashboardGoal,
                newItem: DashboardGoal
            ) = oldItem.title == newItem.title && oldItem.isSelected == newItem.isSelected
        }
    }
}