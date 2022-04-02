package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.databinding.ItemFunctionalAllListBinding
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toUiDate
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.util.*

/**
 * Created by Jasurbek Kurganbaev on 26.06.2021 15:15
 **/
class AllTaskAdapter(
    private val onTaskClick: (Int) -> Unit
) : ListAdapter<FolderItem, AllTaskAdapter.VH>(ITEM_ALL_FUNCTIONAL_TASK_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemFunctionalAllListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: ItemFunctionalAllListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: FolderItem) = bindItem {
            binding.apply {
                root.setOnClickListener {
                    onTaskClick.invoke(task.id)
                }
                if (absoluteAdapterPosition == 0) {
                    relativeLayout.visible()
                } else {
                    relativeLayout.gone()
                }

                tvDescription.isVisible = !task.description.isNullOrBlank()
                imgImprotance.isVisible = !task.importance.isNullOrBlank()
                imgStatus.isVisible = task.status != null
                tvStartTime.isVisible = !task.startTime.isNullOrBlank()
                tvEndTime.isVisible = !task.endTime.isNullOrBlank()
                img4.isVisible = !task.startTime.isNullOrBlank() && !task.endTime.isNullOrBlank()
                img6.isVisible = !task.performer.isNullOrBlank()
                tvName.isVisible = !task.performer.isNullOrBlank()
                cons1.isVisible = (task.filesCount != null && task.filesCount != 0) ||
                        (task.messagesCount != null && task.messagesCount != 0)

                tvListCount.text = currentList.size.toString()
                tvProjectId.text = "#" + task.id
                tvStartTime.text = task.startTime?.toUiDate()
                tvEndTime.text = task.endTime?.toUiDate()
                tvAssigment.text = task.title
                tvName.text = task.performer
                tvDescription.text = task.description
                tv6.text = task.filesCount.toString()
                tv7.text = task.messagesCount.toString()
                task.getDeadlineColor(root.context.resources)?.let {
                    tvEndTime.setTextColor(it.first)
                    tvEndTime.setBackgroundColor(it.second)
                }
                when (task.importance?.lowercase(Locale.ROOT)) {
                    "green" -> binding.imgImprotance.setImageResource(R.drawable.ic_file_text_green)
                    "yellow" -> binding.imgImprotance.setImageResource(R.drawable.ic_file_text_yellow)
                    "red" -> binding.imgImprotance.setImageResource(R.drawable.ic_file_text_red)
                }
                when (task.status?.id) {
                    1 -> binding.imgStatus.apply {
                        setImageResource(R.drawable.ic_new)
                        isVisible = true
                    }
                    2 -> binding.imgStatus.apply {
                        setImageResource(R.drawable.ic_play_circle)
                        isVisible = true
                    }
                    3 -> binding.imgStatus.apply {
                        setImageResource(R.drawable.ic_check_circle)
                        isVisible = true
                    }
                    else -> binding.imgStatus.isVisible = false
                }
            }
        }
    }

    companion object {
        var ITEM_ALL_FUNCTIONAL_TASK_CALLBACK = object : DiffUtil.ItemCallback<FolderItem>() {
            override fun areItemsTheSame(oldItem: FolderItem, newItem: FolderItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FolderItem, newItem: FolderItem) =
                oldItem == newItem
        }
    }
}