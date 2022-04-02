package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.create_worker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemChosenPositionBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem


/**
 * Created by Raximjanov Davronbek on 30.06.2021
 **/

class ChosenPositionAdapter :
    ListAdapter<StructurePositionsResponse.PositionsItem, ChosenPositionAdapter.VH>(
        DIFF_TEAM_CALLBACK
    ) {

    private var listenerClickItem: SingleBlock<StructurePositionsResponse.PositionsItem>? = null
    private var listenerClickDetail: SingleBlock<StructurePositionsResponse.PositionsItem>? = null
    private var listenerClear: SingleBlock<StructurePositionsResponse.PositionsItem>? = null

    companion object {
        var DIFF_TEAM_CALLBACK =
            object : DiffUtil.ItemCallback<StructurePositionsResponse.PositionsItem>() {
                override fun areItemsTheSame(
                    oldItem: StructurePositionsResponse.PositionsItem,
                    newItem: StructurePositionsResponse.PositionsItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: StructurePositionsResponse.PositionsItem,
                    newItem: StructurePositionsResponse.PositionsItem
                ): Boolean {
                    return newItem.title == oldItem.title && newItem.hierarchy == oldItem.hierarchy
                }
            }
    }

    inner class VH(val binding: ItemChosenPositionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {

            val data = getItem(absoluteAdapterPosition)

            binding.apply {
                txtTitle.text = data.title
                btnClear.setOnClickListener {
                    listenerClear?.invoke(data)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemChosenPositionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun clearOnClickListener(block: SingleBlock<StructurePositionsResponse.PositionsItem>) {
        listenerClear = block
    }
}