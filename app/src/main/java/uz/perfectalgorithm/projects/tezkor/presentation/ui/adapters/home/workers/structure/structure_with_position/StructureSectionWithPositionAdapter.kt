package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure.structure_with_position

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStrcSectionPositionBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Davronbek Raximjanov on 30.06.2021
 **/

class StructureSectionWithPositionAdapter :
    ListAdapter<StructurePositionsResponse.DataItem, StructureSectionWithPositionAdapter.VH>(
        DIFF_CHANNEL_CALLBACK
    ) {

    private var listenerCallback: DoubleBlock<StructurePositionsResponse.DataItem, Boolean>? = null

    companion object {
        var DIFF_CHANNEL_CALLBACK =
            object : DiffUtil.ItemCallback<StructurePositionsResponse.DataItem>() {
                override fun areItemsTheSame(
                    oldItem: StructurePositionsResponse.DataItem,
                    newItem: StructurePositionsResponse.DataItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: StructurePositionsResponse.DataItem,
                    newItem: StructurePositionsResponse.DataItem
                ): Boolean {
                    return newItem.title == oldItem.title
                }
            }
    }

    inner class VH(val binding: ItemStrcSectionPositionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var sectionAdapter: StructureSectionWithPositionAdapter
        private lateinit var positionAdapter: StructurePositionAdapter

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)

                rvPositions.layoutManager = LinearLayoutManager(App.instance)
                rvSection.layoutManager = LinearLayoutManager(App.instance)

                sectionAdapter = StructureSectionWithPositionAdapter()
                positionAdapter = StructurePositionAdapter()

                positionAdapter.submitList(data.positions)

                sectionAdapter.submitList(data.children!!)
                rvSection.adapter = sectionAdapter
                rvPositions.adapter = positionAdapter


                txtSectionTitle.text = data.title

                rootLayout.setOnClickListener {
                    setVisibility()
                }
                imgDropDown.setOnClickListener {
                    setVisibility()
                }
            }
        }

        private fun setVisibility() {
            val boolVisibility =
                binding.rvPositions.visibility == View.VISIBLE

            listenerCallback?.invoke(getItem(absoluteAdapterPosition), boolVisibility)

            if (boolVisibility) {
                binding.rvSection.gone()
                binding.rvPositions.gone()
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_up
                    )
                )
            } else {
                binding.rvSection.visible()
                binding.rvPositions.visible()
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_down
                    )
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStrcSectionPositionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: DoubleBlock<StructurePositionsResponse.DataItem, Boolean>) {
        listenerCallback = block
    }


}