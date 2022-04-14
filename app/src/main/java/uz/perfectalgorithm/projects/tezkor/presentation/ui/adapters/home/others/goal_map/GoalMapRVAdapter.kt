package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.goal_map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemGoalMapBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem


class GoalMapRVAdapter : ListAdapter<ItemGoalMapData, GoalMapRVAdapter.VH>(ITEM_GOAL_MAP_CALLBACK) {
    private var itemClickListener: SingleBlock<ItemGoalMapData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGoalMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnItemGoalMapClickListener(block: SingleBlock<ItemGoalMapData>) {
        itemClickListener = block
    }

    inner class VH(private val binding: ItemGoalMapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //        private lateinit var projectAdapter: MapProjectAdapter
        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                tvTitle.text = data.title

                itemGoalMapLayout.setOnClickListener {
                    itemClickListener?.invoke(data)
                }
            }
        }
    }


    companion object {
        var ITEM_GOAL_MAP_CALLBACK = object : DiffUtil.ItemCallback<ItemGoalMapData>() {
            override fun areItemsTheSame(
                oldItem: ItemGoalMapData,
                newItem: ItemGoalMapData
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: ItemGoalMapData,
                newItem: ItemGoalMapData
            ): Boolean {
                return newItem.title == oldItem.title &&
                        newItem.id == oldItem.id
            }

        }
    }


}