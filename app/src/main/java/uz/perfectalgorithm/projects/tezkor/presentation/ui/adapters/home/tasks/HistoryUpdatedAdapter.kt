package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.UpdatedHistory
import uz.perfectalgorithm.projects.tezkor.databinding.ItemCommentTaskBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.gone

class HistoryUpdatedAdapter :
    ListAdapter<UpdatedHistory, HistoryUpdatedAdapter.HistoryVH>(ITEM_ALL_FUNCTIONAL_TASK_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
        return HistoryVH(
            ItemCommentTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryVH, position: Int) {
        val item = getItem(position)
        holder.ivImage.loadImageUrl(item.image ?: "")
        holder.tvContent.text = "Vazifani statusi o'zgardi"
        holder.tvDate.text = item.changedDate
        holder.tvName.text = item.changer
    }

    inner class HistoryVH(binding: ItemCommentTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivImage = binding.imageAvatar
        val tvName = binding.commentAuthorFullName
        val tvDate = binding.commentedDate
        val tvContent = binding.commentText

        init {
            binding.replyButton.gone()
        }
    }

    companion object {
        var ITEM_ALL_FUNCTIONAL_TASK_CALLBACK = object : DiffUtil.ItemCallback<UpdatedHistory>() {
            override fun areItemsTheSame(oldItem: UpdatedHistory, newItem: UpdatedHistory) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UpdatedHistory, newItem: UpdatedHistory) =
                oldItem == newItem
        }
    }
}