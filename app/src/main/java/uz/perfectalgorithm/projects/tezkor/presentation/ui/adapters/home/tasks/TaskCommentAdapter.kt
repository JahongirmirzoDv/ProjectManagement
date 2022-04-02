package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskCommentData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemCommentTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.inVisible
import java.util.*

/**
 * Created by Jasurbek Kurganbaev on 10/28/2021 10:59 AM
 **/
class TaskCommentAdapter(
) : ListAdapter<TaskCommentData, TaskCommentAdapter.VH>(ITEM_ALL_FUNCTIONAL_TASK_CALLBACK) {
    private val replyCommentAdapter by lazy { TaskReplyAdapter() }

    //    private var itemShowClickListener: EmptyBlock? = null
    private var itemCommentClickListener: SingleBlock<TaskCommentData>? = null

/*    fun setOnClickItemShowCommentButtonListener(block: EmptyBlock) {
        itemShowClickListener = block
    }*/

    fun setOnClickCommentClickListener(block: SingleBlock<TaskCommentData>) {
        itemCommentClickListener = block
    }


    companion object {
        var ITEM_ALL_FUNCTIONAL_TASK_CALLBACK = object : DiffUtil.ItemCallback<TaskCommentData>() {
            override fun areItemsTheSame(oldItem: TaskCommentData, newItem: TaskCommentData) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TaskCommentData, newItem: TaskCommentData) =
                oldItem == newItem
        }
    }


    inner class VH(private val binding: ItemCommentTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                commentAuthorFullName.text = data.commenter
                val resultDate =
                    data?.createdAt?.replace("-", ".")?.split("T")?.get(0)
                commentedDate.text = resultDate
                imageAvatar.loadImageUrl(data.avatar?:"")
                commentText.text = data.text
                if (data.repliedComments?.size!! >= 1) {
                    showRepliesButton.show()
                }
                showRepliesButton.text = "Show ${data.repliedComments?.size} replied"

                if (!data.repliedComments.isNullOrEmpty()) {
                    commentReplyList.adapter = replyCommentAdapter
                    replyCommentAdapter.submitList(data.repliedComments)
                }

                hideButton.setOnClickListener {
                    commentReplyList.gone()
                    showRepliesButton.show()
                    hideButton.gone()
                }

                showRepliesButton.setOnClickListener {
                    commentReplyList.show()
                    showRepliesButton.inVisible()
                    hideButton.show()
                }

                replyButton.setOnClickListener {
                    itemCommentClickListener?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemCommentTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()
}