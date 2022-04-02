package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.Project
import uz.perfectalgorithm.projects.tezkor.databinding.ProjectItemBinding
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toUiDate
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.VerticalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem
import java.util.*

/**
 *Created by farrukh_kh on 8/5/21 10:06 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.tasks.projects
 **/
class ProjectItemAdapter(
    context: Context,
    dragHelper: DragHelper,
    dragDropListener: DragDropListener,
    private val onProjectClick: (Int) -> Unit
) : VerticalAdapter<ProjectItemAdapter.VH>(context, dragHelper, dragDropListener) {

    private val storage by lazy { LocalStorage.instance }

    inner class VH(private val binding: ProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Project) = with(binding) {
            root.setOnClickListener {
                onProjectClick.invoke(item.id)
            }
            if (storage.userId == item.creatorId || storage.userId == item.performerId) {
                root.setOnLongClickListener {
                    dragItem(this@VH)
                    true
                }
            }
            tvProjectId.text = "#${item.id}"
            tvProjectName.text = item.title
            tvResponsibleValue.text = item.leader
            tvTimeToValue.text = item.endTime.toUiDate()
            tvProjectName.isSelected = true
            tvResponsibleValue.isSelected = true
            item.getDeadlineColor(root.context.resources).let {
                tvTimeToValue.setTextColor(it.first)
                tvTimeToValue.setBackgroundColor(it.second)
            }
//            when (item.status?.id) {
//                1 -> {
//                    imgProjectStatus.isVisible = true
//                    imgProjectStatus.setImageResource(R.drawable.ic_new)
//                }
//                2 -> {
//                    imgProjectStatus.isVisible = true
//                    imgProjectStatus.setImageResource(R.drawable.ic_play_circle)
//                }
//                3 -> {
//                    imgProjectStatus.isVisible = true
//                    imgProjectStatus.setImageResource(R.drawable.ic_check_circle)
//                }
//                else -> imgProjectStatus.isVisible = false
//            }
            when (item.importance) {
                "red" -> cardImportance.setCardBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.project_status_red_color
                    )
                )
                "yellow" -> cardImportance.setCardBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.project_status_yellow_color
                    )
                )
                "green" -> cardImportance.setCardBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.project_status_green_color
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(context: Context?, holder: VH, item: DragItem, position: Int) {
        holder.bind(item as Project)
    }
}