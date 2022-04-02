package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.add_members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemAddedMembersGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal

/**
 * Created by Raximjanov Davronbek on 07.10.2021 16:44
 **/

class AddedMembersForCreateGroupChatAdapter :
    ListAdapter<AllWorkersShortDataResponse.WorkerShortDataItem, AddedMembersForCreateGroupChatAdapter.VH>(
        DIFF_TEAM_CALLBACK
    ) {

    private var listenClick: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>? = null
    private var listenDelete: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>? = null

    companion object {
        var DIFF_TEAM_CALLBACK =
            object : DiffUtil.ItemCallback<AllWorkersShortDataResponse.WorkerShortDataItem>() {
                override fun areItemsTheSame(
                    oldItem: AllWorkersShortDataResponse.WorkerShortDataItem,
                    newItem: AllWorkersShortDataResponse.WorkerShortDataItem
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: AllWorkersShortDataResponse.WorkerShortDataItem,
                    newItem: AllWorkersShortDataResponse.WorkerShortDataItem
                ): Boolean {
                    return newItem.fullName == oldItem.fullName && newItem.image == oldItem.image && newItem.isChecked == oldItem.isChecked
                }
            }
    }

    inner class VH(val binding: ItemAddedMembersGroupChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {

            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                data?.let { data ->
                    txtFullName.text = data.fullName
                    data.image?.let { imgPerson.loadImageUrlUniversal(it, R.drawable.ic_user) }

                    imgDelete.setOnClickListener {
                        listenDelete?.invoke(data)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemAddedMembersGroupChatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        listenClick = block
    }

    fun setOnDeleteListener(block: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        listenDelete = block
    }

}