package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.TaskCommentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskCommentData
import uz.perfectalgorithm.projects.tezkor.databinding.ItemCommentTaskBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemReplyCommentTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem


class TaskReplyAdapter(
) : ListAdapter<TaskCommentData, TaskReplyAdapter.VH>(ITEM_ALL_FUNCTIONAL_TASK_CALLBACK) {

    private var itemShowClickListener: EmptyBlock? = null
    private var itemCommentClickListener: SingleBlock<TaskCommentRequest>? = null

    fun setOnClickCommentClickListener(block: SingleBlock<TaskCommentRequest>) {
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


    inner class VH(private val binding: ItemReplyCommentTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                commentReplierFullName.text = data.commenter
                commentAskerFullName.text = data.commenter
                val resultDate =
                    data?.createdAt?.replace("-", ".")?.split("T")?.get(0)
                replyCommentedDate.text = resultDate
                replyCommentText.text = data.text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemReplyCommentTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()
}