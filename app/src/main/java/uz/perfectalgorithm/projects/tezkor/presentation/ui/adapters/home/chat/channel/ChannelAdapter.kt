package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.channel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemChannelBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem


/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 11:57
 **/
class ChannelAdapter : ListAdapter<ChannelData, ChannelAdapter.VH>(DIFF_CHANNEL_CALLBACK) {

    private var listenerCallback: SingleBlock<ChannelData>? = null


    companion object {
        var DIFF_CHANNEL_CALLBACK = object : DiffUtil.ItemCallback<ChannelData>() {
            override fun areItemsTheSame(oldItem: ChannelData, newItem: ChannelData): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: ChannelData, newItem: ChannelData): Boolean {
                return newItem.admin == oldItem.admin && newItem.creator == oldItem.creator &&
                        newItem.title == newItem.title && newItem.members == oldItem.members
            }

        }
    }

    fun setOnClickListener(block: SingleBlock<ChannelData>) {
        listenerCallback = block
    }

    inner class VH(val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            listenerCallback?.invoke(getItem(absoluteAdapterPosition))
        }

        fun bind() = bindItem {
            binding.apply {
                val data = getItem(absoluteAdapterPosition)
//                channelImage.loadImageUrl(data.image as String)
                tvChannelName.text = data.title
                tvCommands.text = "Buyruq#${data.id}"
                tvLastPostCommand.text = data.createdAt

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

}