package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.contact_structure

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemContactStructureWorkerBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 * Created by Davronbek Raximjanov on 22.06.2021
 **/

class ContactStructureWorkerAdapter(
    private val positionsItem: StructureResponse.PositionsItem,
) :
    ListAdapter<AllWorkersResponse.DataItem, ContactStructureWorkerAdapter.VH>(DIFF_CHANNEL_CALLBACK) {

    private var workerCheckedClickListener: SingleBlock<AllWorkersResponse.DataItem>? = null


    companion object {
        var DIFF_CHANNEL_CALLBACK =
            object : DiffUtil.ItemCallback<AllWorkersResponse.DataItem>() {
                override fun areItemsTheSame(
                    oldItem: AllWorkersResponse.DataItem,
                    newItem: AllWorkersResponse.DataItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: AllWorkersResponse.DataItem,
                    newItem: AllWorkersResponse.DataItem
                ): Boolean {
                    return newItem.firstName == oldItem.firstName
                }
            }
    }


    inner class VH(val binding: ItemContactStructureWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                txtFullName.text = data.firstName + " " + data.lastName
                txtPosition.text = positionsItem.title
                data.image?.let { imgProfile.loadImageUrl(it) }
                itemView.apply {
                    imgChecked.setImageResource(if (data.isChecked) R.drawable.custom_checkbox_checked else R.drawable.custom_checkbox_unchecked)
                }

                rootB.setOnClickListener {

                }


            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemContactStructureWorkerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun checkedToFalse(position: Int) {
        currentList.forEach {
            it.isChecked = false
        }
        currentList[position].isChecked = true
        notifyDataSetChanged()
    }


}