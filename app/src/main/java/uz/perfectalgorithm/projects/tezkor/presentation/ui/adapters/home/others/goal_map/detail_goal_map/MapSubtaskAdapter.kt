package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map.detail_goal_map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.TasksItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemGoalMapProjectTaskSubtaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 16.07.2021 16:32
 **/
class MapSubtaskAdapter(private val onTaskClick: SingleBlock<Int>) :
    ListAdapter<TasksItem, MapSubtaskAdapter.VH>(ITEM_MAP_SUBTASK_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGoalMapProjectTaskSubtaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    inner class VH(private val binding: ItemGoalMapProjectTaskSubtaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtSubtaskName.text = data.title
                txtSubtaskPercent.text = data.performedPercent.toString() + "%"

                btnEditGoal.setOnClickListener {
                    onTaskClick(data.id!!)
                }
            }
        }
    }

    companion object {
        var ITEM_MAP_SUBTASK_CALLBACK = object : DiffUtil.ItemCallback<TasksItem>() {
            override fun areItemsTheSame(oldItem: TasksItem, newItem: TasksItem): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: TasksItem, newItem: TasksItem): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.title == oldItem.title &&
                        newItem.tasksCount == oldItem.tasksCount &&
                        newItem.tasks == oldItem.tasks
            }

        }
    }


}