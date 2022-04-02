package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map.detail_goal_map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalsItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemGoalMapGoalBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 16.07.2021 16:26
 **/
class DetailGoalMapGoalProjectTaskSubtaskAdapter(
    private val onGoalClick: SingleBlock<Int>,
    private val onProjectClick: SingleBlock<Int>,
    private val onTaskClick: SingleBlock<Int>
) :
    ListAdapter<GoalsItem, DetailGoalMapGoalProjectTaskSubtaskAdapter.VH>(
        ITEM_GOAL_MAP_GOAL_PROJECT_TASK_SUBTASK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGoalMapGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()


    inner class VH(private val binding: ItemGoalMapGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var projectAdapter: MapProjectAdapter

        fun bind() = bindItem {
            binding.apply {
                projectAdapter = MapProjectAdapter(onProjectClick, onTaskClick)
                val data = getItem(absoluteAdapterPosition)

                btnEditGoal.setOnClickListener {
                    onGoalClick.invoke(data.id ?: throw NullPointerException("There is no goal id"))
                }


                txtGoalName.text = data.title
                txtGoalMapGoalPercent.text = data.performedPercent.toString() + "%"
                txtGoalMapProjectCount.text = data.projectsCount.toString() + " ta"
                projectAdapter.submitList(data.projects)
                rvProject.adapter = projectAdapter

                itemGoalLayout.setOnClickListener {
                    if (rvProject.visibility == View.GONE) {
                        Log.d("CCC", "if")
                        rvProject.visibility = View.VISIBLE
                        imgDrop.setImageResource(R.drawable.ic_chevron_up)
                    } else {
                        Log.d("CCC", "else")
                        rvProject.visibility = View.GONE
                        imgDrop.setImageResource(R.drawable.ic_chevron_down)
                    }
                }
            }
        }
    }

    companion object {
        var ITEM_GOAL_MAP_GOAL_PROJECT_TASK_SUBTASK =
            object : DiffUtil.ItemCallback<GoalsItem>() {
                override fun areItemsTheSame(
                    oldItem: GoalsItem,
                    newItem: GoalsItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: GoalsItem,
                    newItem: GoalsItem
                ): Boolean {
                    return newItem.id == oldItem.id &&
                            newItem.title == oldItem.title &&
                            newItem.projectsCount == oldItem.projectsCount &&
                            newItem.performedPercent == oldItem.performedPercent
                }
            }
    }


}
