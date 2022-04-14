package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map.detail_goal_map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.TasksItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemGoalMapGoalProjectTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem


class MapTaskAdapter(private val onTaskClick: SingleBlock<Int>) :
    ListAdapter<TasksItem, MapTaskAdapter.VH>(ITEM_MAP_TASK_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGoalMapGoalProjectTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()


    companion object {
        var ITEM_MAP_TASK_CALLBACK = object : DiffUtil.ItemCallback<TasksItem>() {
            override fun areItemsTheSame(oldItem: TasksItem, newItem: TasksItem): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: TasksItem, newItem: TasksItem): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.title == oldItem.title &&
                        newItem.tasks == oldItem.tasks &&
                        newItem.tasksCount == oldItem.tasksCount
            }

        }
    }

    inner class VH(private val binding: ItemGoalMapGoalProjectTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var subtaskAdapter: MapSubtaskAdapter

        fun bind() = bindItem {
            binding.apply {
                subtaskAdapter = MapSubtaskAdapter(onTaskClick)
                val data = getItem(absoluteAdapterPosition)
                txtTaskName.text = data.title
                txtSubtaskPercent.text = data.performedPercent.toString() + "%"
                txtSubtaskCount.text = data.tasksCount.toString() + " ta"
                subtaskAdapter.submitList(data.tasks)
                rvSubtasks.adapter = subtaskAdapter

                itemTaskItemLayout.setOnClickListener {
                    if (rvSubtasks.visibility == View.GONE) {
                        Log.d("CCC", "if")
                        rvSubtasks.visibility = View.VISIBLE
                        imgDrop.setImageResource(R.drawable.ic_chevron_up)
                    } else {
                        Log.d("CCC", "else")

                        rvSubtasks.visibility = View.GONE
                        imgDrop.setImageResource(R.drawable.ic_chevron_down)
                    }
                }

                btnEditGoal.setOnClickListener {
                    onTaskClick(data.id!!)
                }
            }

        }
    }


}