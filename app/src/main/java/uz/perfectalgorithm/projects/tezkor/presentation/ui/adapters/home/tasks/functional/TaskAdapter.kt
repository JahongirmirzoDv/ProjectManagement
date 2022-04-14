package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.databinding.TaskItemBinding
import uz.perfectalgorithm.projects.tezkor.utils.setTint
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toUiDate
import uz.perfectalgorithm.projects.tezkor.utils.translateStatusTask
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.VerticalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem
import java.util.*


class TaskAdapter(
    context: Context,
    dragHelper: DragHelper,
    dragDropListener: DragDropListener,
    private val onTaskClick: (Int) -> Unit
) : VerticalAdapter<TaskAdapter.VH>(context, dragHelper, dragDropListener) {

    private val storage by lazy { LocalStorage.instance }

    inner class VH(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FolderItem) = with(binding) {
            root.setOnClickListener {
                onTaskClick.invoke(item.id)
            }
            if (storage.userId == item.creatorId || storage.userId == item.performerId) {
                root.setOnLongClickListener {
                    dragItem(this@VH)
                    true
                }
            }

//            tvDescription.isVisible = !item.description.isNullOrBlank()
            imgImportance.isVisible = !item.importance.isNullOrBlank()
//            imgStatus.isVisible = item.status != null
            llDate.isVisible = !item.startTime.isNullOrBlank() && !item.endTime.isNullOrBlank()
            img3.isVisible = !item.performer.isNullOrBlank()
            tvFullName.isVisible = !item.performer.isNullOrBlank()
            img4.isVisible = item.filesCount != null && item.filesCount != 0
            tvFileCount.isVisible = item.filesCount != null && item.filesCount != 0
            img5.isVisible = item.messagesCount != null && item.messagesCount != 0
            tvCommetCount.isVisible = item.messagesCount != null && item.messagesCount != 0

            tvTitle.text = item.title
            tvDescription.text = item.description
            tvDate.text = Pair(item.startTime ?: "", item.endTime ?: "").toUiDate()
            tvFullName.text = item.performer
            tvFileCount.text = item.filesCount.toString()
            tvCommetCount.text = item.messagesCount.toString()
            imgStatusTitle.text = (item.internal_status?:"new").translateStatusTask(storage.lan)
            item.getDeadlineColor(root.context.resources)?.let {
                ivDate.setTint(it.first)
                tvDate.setTextColor(it.first)
                ivDate.setBackgroundColor(it.second)
                tvDate.setBackgroundColor(it.second)
            }
            when (item.importance?.toLowerCase(Locale.ROOT)) {
                "red" -> {
                    binding.imgImportance.setImageResource(R.drawable.ic_file_text_red)
                }
                "yellow" -> {
                    binding.imgImportance.setImageResource(R.drawable.ic_file_text_yellow)
                }
                "green" -> {
                    binding.imgImportance.setImageResource(R.drawable.ic_file_text_green)
                }
                else -> {
                    binding.imgImportance.setImageResource(R.drawable.ic_file_text)
                }
            }
            when (item.internal_status?:"") {
                "new" -> binding.imgStatus.apply {
                    setImageResource(R.drawable.ic_new)
                    isVisible = true
                }
                "began" -> binding.imgStatus.apply {
                    setImageResource(R.drawable.ic_play_circle)
                    isVisible = true
                }
                "confirmed" -> binding.imgStatus.apply {
                    setImageResource(R.drawable.ic_check_circle)
                    isVisible = true
                }
                else -> binding.imgStatus.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(context: Context?, holder: VH, item: DragItem, position: Int) {
        holder.bind(item as FolderItem)
    }
}