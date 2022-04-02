package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.workers_

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTeamWorkersForChatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlWithPlaceholder
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Raximjanov Davronbek on 20.08.2021
 **/

class WorkerListForChatAdapter :
    ListAdapter<AllWorkersResponse.DataItem, WorkerListForChatAdapter.VH>(DIFF_TEAM_CALLBACK) {

    private var listenClick: SingleBlock<AllWorkersResponse.DataItem>? = null

    private var lastCapLetter = ';'

    companion object {
        var DIFF_TEAM_CALLBACK = object : DiffUtil.ItemCallback<AllWorkersResponse.DataItem>() {
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
                return newItem.firstName == oldItem.firstName && newItem.birthDate == oldItem.birthDate
            }
        }
    }

    inner class VH(val binding: ItemTeamWorkersForChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {

            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                var a = ';'
                data?.let { data ->
                    if (!data.firstName.isNullOrBlank()) {
                        a = data.firstName!!.trim()[0].toLowerCase()
                    }
                    if (a != lastCapLetter.toLowerCase()) {
                        lastCapLetter = a
                        tvTitle.text = a.toString().toUpperCase()
                        tvTitle.visible()
                    } else {
                        tvTitle.gone()
                    }
                    txtFullName.text = data.firstName + " " + data.lastName

                    data.image?.let { imgPerson.loadImageUrlWithPlaceholder(it, R.drawable.ic_user) }

                }

                if (data.isChecked) {
                    ll1.visible()
                } else {
                    ll1.gone()
                }

                binding.cons1.setOnClickListener {
                    listenClick?.invoke(data)
                }

                binding.imgDropDown.setOnClickListener {
                    listenClick?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemTeamWorkersForChatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: SingleBlock<AllWorkersResponse.DataItem>) {
        listenClick = block
    }

}