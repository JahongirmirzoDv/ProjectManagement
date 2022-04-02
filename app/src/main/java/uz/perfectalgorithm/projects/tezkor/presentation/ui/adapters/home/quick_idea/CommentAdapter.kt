package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.CommentData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemIdeaCommentReceivedBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemIdeaCommentSendedBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show

/**
 * Created by Jasurbek Kurganbaev on 04.08.2021 13:28
 **/
class CommentAdapter(private val userId: Int?) :
    PagingDataAdapter<CommentData, RecyclerView.ViewHolder>(IDEA_COMMENT_ITEM_CALLBACK) {


    companion object {
        var IDEA_COMMENT_ITEM_CALLBACK = object : DiffUtil.ItemCallback<CommentData>() {
            override fun areItemsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
                return newItem.id == oldItem.id &&
                        newItem.createdAt == oldItem.createdAt &&
                        newItem.idea == oldItem.idea &&
                        newItem.text == oldItem.text &&
                        newItem.user == oldItem.user
            }

        }
    }

    private var listenClick: DoubleBlock<CommentData, View>? =
        null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.user?.id) {
            userId -> {
                1
            }
            else -> {
                2
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val binding = ItemIdeaCommentSendedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IdeaCommentSentHolder(binding)
            }
            else -> {
                val binding = ItemIdeaCommentReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IdeaCommentReceivedHolder(binding)

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)?.user?.id) {
            userId -> {
                val viewholder = holder as IdeaCommentSentHolder
                viewholder.bind(position)
            }
            else -> {
                val viewholder = holder as IdeaCommentReceivedHolder
                viewholder.bind(position)
            }
        }
    }

    inner class IdeaCommentSentHolder(val binding: ItemIdeaCommentSendedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val sendMessageData = getItem(position)
            binding.apply {
                txtMessageContent.text = sendMessageData?.text
                if (position == 0) {
                    consDate.show()
                    val resultDate =
                        sendMessageData?.createdAt?.replace("-", ".")?.split("T")?.get(0)
                    tvDate.text = resultDate
                }

                /* val time = sendMessageData?.createdAt?.replace("-", ".")
                val resultDate = time?.split("T")?.get(0)
                txtTime.text = resultDate */

                txtSenderName.text =
                    sendMessageData?.user?.firstName + " " + sendMessageData?.user?.lastName
            }
        }
    }

    inner class IdeaCommentReceivedHolder(val binding: ItemIdeaCommentReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val receivedMessageData = getItem(position)
            binding.apply {
                txtMessageContent.text = receivedMessageData?.text

                /*val time = receivedMessageData?.createdAt?.replace("-", ".")
                val resultDate = time?.split("T")?.get(0)
                tvDate.text = resultDate*/

                txtReceiverName.text =
                    receivedMessageData?.user?.firstName + " " + receivedMessageData?.user?.lastName
                imgPersonPhoto.loadImageUrl(receivedMessageData?.user?.image!!)
            }
        }
    }

    fun setOnclickListener(f: DoubleBlock<CommentData, View>) {
        listenClick = f
    }

}
