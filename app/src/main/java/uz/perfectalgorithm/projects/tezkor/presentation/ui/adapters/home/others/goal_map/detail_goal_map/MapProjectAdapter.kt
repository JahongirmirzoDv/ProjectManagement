package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map.detail_goal_map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ProjectsItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemGoalMapGoalProjectBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Jasurbek Kurganbaev on 16.07.2021 16:32
 **/
class MapProjectAdapter(
    private val onProjectClick: SingleBlock<Int>,
    private val onTaskClick: SingleBlock<Int>
) :
    ListAdapter<ProjectsItem, MapProjectAdapter.VH>(ITEM_MAP_PROJECT_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGoalMapGoalProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    inner class VH(private val binding: ItemGoalMapGoalProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var mapTaskAdapter: MapTaskAdapter

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtProjectName.text = data.title
                txtTaskPercent.text = data.performedPercent.toString() + "%"
                txtTaskCount.text = data.tasksCount.toString() + " ta"
                mapTaskAdapter = MapTaskAdapter(onTaskClick)
                mapTaskAdapter.submitList(data.tasks)
                rvTasks.adapter = mapTaskAdapter


                itemProject.setOnClickListener {
                    if (rvTasks.visibility == View.GONE) {
                        rvTasks.visibility = View.VISIBLE
                        imgDrop.setImageResource(R.drawable.ic_chevron_up)
                    } else {

                        rvTasks.visibility = View.GONE
                        imgDrop.setImageResource(R.drawable.ic_chevron_down)
                    }
                }

                btnEditGoal.setOnClickListener {
                    onProjectClick(data.id!!)
                }
            }
        }
    }

    companion object {
        var ITEM_MAP_PROJECT_CALLBACK = object : DiffUtil.ItemCallback<ProjectsItem>() {
            override fun areItemsTheSame(oldItem: ProjectsItem, newItem: ProjectsItem): Boolean {
                return newItem.id == oldItem.id

            }

            override fun areContentsTheSame(oldItem: ProjectsItem, newItem: ProjectsItem): Boolean {
                return newItem.title == oldItem.title &&
                        newItem.performedPercent == oldItem.performedPercent &&
                        newItem.id == oldItem.id

            }


        }
    }


}