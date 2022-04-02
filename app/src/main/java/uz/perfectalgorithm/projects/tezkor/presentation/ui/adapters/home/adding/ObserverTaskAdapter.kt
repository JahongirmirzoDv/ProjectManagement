package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemParticipantsBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

/**
 * Created by Jasurbek Kurganbaev on 05.07.2021 16:03
 **/
class ObserverTaskAdapter :
    ListAdapter<PersonData, ObserverTaskAdapter.VH>(ITEM_FUNC_TASK_ITEM) {

    private var itemDeleteClickListener: SingleBlock<PersonData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemParticipantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()


    fun setOnItemDeleteClickListener(block: SingleBlock<PersonData>) {
        itemDeleteClickListener = block
    }

    companion object {
        var ITEM_FUNC_TASK_ITEM =
            object : DiffUtil.ItemCallback<PersonData>() {
                override fun areItemsTheSame(
                    oldItem: PersonData,
                    newItem: PersonData
                ): Boolean {
                    return newItem.id == oldItem.id
                }

                override fun areContentsTheSame(
                    oldItem: PersonData,
                    newItem: PersonData
                ): Boolean {
                    return newItem == oldItem
                }

            }
    }

    inner class VH(private val binding: ItemParticipantsBinding) :
        RecyclerView.ViewHolder(binding.rootL) {

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                tvPersonName.text = data.fullName
                participantImageAvatar.loadImageUrl(data.image ?: return@apply)
                imgDelete.setOnClickListener {

                    itemDeleteClickListener?.invoke(data)
                }
            }
        }
    }


}
