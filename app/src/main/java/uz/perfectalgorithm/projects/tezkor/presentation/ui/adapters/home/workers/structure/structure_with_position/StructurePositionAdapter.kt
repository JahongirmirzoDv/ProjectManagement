package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure.structure_with_position

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemPositionStrcBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.ChoosePositionDialog
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Davronbek Raximjanov on 30.06.2021
 **/

class StructurePositionAdapter() :
    ListAdapter<StructurePositionsResponse.PositionsItem, StructurePositionAdapter.VH>(
        DIFF_CHANNEL_CALLBACK
    ) {


    companion object {
        var DIFF_CHANNEL_CALLBACK =
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
                    return newItem.title == oldItem.title
                }
            }
    }


    inner class VH(val binding: ItemPositionStrcBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtTitle.text = data.title
                txtHierarchy.text = data.hierarchy

                rootB.setOnClickListener {

                    if (imgNoChecked.visibility == View.VISIBLE) {
                        imgNoChecked.gone()
                        ChoosePositionDialog.chosenPositions.add(data)
                    } else {

                        imgNoChecked.visible()
                        ChoosePositionDialog.chosenPositions.remove(data)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPositionStrcBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }
}