package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.compass

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.ChatModel
import uz.perfectalgorithm.projects.tezkor.databinding.ItemPersonalChatUserBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem

/**
 * Created by Davronbek Raximjanov on 22.07.2021 15:45
 **/

class CompassChatAdapter :
    ListAdapter<ChatModel, CompassChatAdapter.VH>(DIFF_PROJECT_CALLBACK) {

    private var listenerCallback: SingleBlock<ChatModel>? = null

    companion object {
        var DIFF_PROJECT_CALLBACK = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(
                oldItem: ChatModel,
                newItem: ChatModel
            ): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: ChatModel,
                newItem: ChatModel
            ): Boolean {
                return newItem.receiver?.firstName == oldItem.receiver?.firstName && newItem.receiver?.lastName == oldItem.receiver?.lastName
            }
        }
    }

    fun setOnClickListener(block: SingleBlock<ChatModel>) {
        listenerCallback = block
    }


    inner class VH(private val binding: ItemPersonalChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                val data = getItem(absoluteAdapterPosition)

                txtUsername.text = data.receiver?.firstName
                txtDate.text = data.messageModel?.sendDate.toString() ?: ""
                txtLastMessage.text = data.messageModel?.messageText ?: ""

                root.setOnClickListener {
                    listenerCallback?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPersonalChatUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }


}