package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.Project
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectsByStatus
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.Status
import uz.perfectalgorithm.projects.tezkor.databinding.FooterAddListBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ProjectEntryBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.HorizontalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem
import kotlin.math.abs



class ProjectStatusAdapter(
    private val context: Context,
    private val onAddProjectClick: () -> Unit,
    private val onAddColumnClick: () -> Unit,
    private val changeItemStatus: (Project, ProjectsByStatus) -> Unit,
    private val onProjectClick: (Int) -> Unit,
    private val onMenuClick: DoubleBlock<Status, View>,
) : HorizontalAdapter<ProjectStatusAdapter.BaseVH>(context) {

    override fun getContentLayoutRes() = R.layout.project_entry

    override fun getFooterLayoutRes() = R.layout.footer_add_list

    override fun onCreateViewHolder(parent: View?, viewType: Int) = when (viewType) {
        DragHelper.TYPE_CONTENT -> VH(
            ProjectEntryBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent as? ViewGroup,
                false
            )
        )
        DragHelper.TYPE_FOOTER -> FooterVH(
            FooterAddListBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent as? ViewGroup,
                false
            )
        )
        else -> throw IllegalArgumentException("There are only 2 viewTypes")
    }

    override fun onBindContentViewHolder(holder: BaseVH, dragColumn: DragColumn?, position: Int) {
        (holder as VH).bind(dragColumn as ProjectsByStatus)
    }

    override fun onBindFooterViewHolder(holder: BaseVH, position: Int) {
        (holder as FooterVH).bind()
    }

    override fun needFooter() = true

    abstract inner class BaseVH(itemView: View) :
        HorizontalAdapter<BaseVH>.ViewHolder(itemView)

    inner class VH(private val binding: ProjectEntryBinding) :
        BaseVH(binding.root) {

        private val dragDropListener = object : DragDropListener {
            override fun onDeleteFromOldList() {
                binding.tvCount.apply {
                    (text.toString().toInt() - 1).let { count ->
                        text = "$count"
                        binding.tvEmpty.isVisible = count == 0
                    }
                }
            }

            override fun onItemDropped(item: DragItem, newPageIndex: Int) {
                binding.tvCount.apply {
                    (text.toString().toInt() + 1).let { count ->
                        text = "$count"
                        binding.tvEmpty.isVisible = count == 0
                    }
                }
                changeItemStatus.invoke(
                    item as Project,
                    getItem(newPageIndex) as ProjectsByStatus
                )
            }
        }

        private val projectItemAdapter by lazy {
            ProjectItemAdapter(
                context,
                dragHelper!!,
                dragDropListener,
                onProjectClick
            )
        }

        // TODO: 8/5/21 needs refactor
        init {
            binding.rvTasks.apply {
                addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {

                    var lastX = 0.0f
                    var lastY = 0.0f

                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        when (e.action) {
                            MotionEvent.ACTION_MOVE -> {
                                if (dragHelper?.isDraggingItem == true) {
                                    rv.parent.requestDisallowInterceptTouchEvent(false)
                                } else {
                                    if (abs(lastX - e.x) < abs(lastY - e.y)) {
                                        rv.parent.requestDisallowInterceptTouchEvent(true)
                                        when {
                                            lastY < e.y && rv.canScrollVertically(-1) -> {
                                                rv.parent.requestDisallowInterceptTouchEvent(true)
                                            }
                                            lastY > e.y && rv.canScrollVertically(1) -> {
                                                rv.parent.requestDisallowInterceptTouchEvent(true)
                                            }
                                            else -> {
                                                rv.parent.requestDisallowInterceptTouchEvent(false)
                                            }
                                        }
                                    }
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                rv.parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }

                        lastX = e.x
                        lastY = e.y
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                })
                adapter = projectItemAdapter
            }
        }

        override fun getRecyclerView() = binding.rvTasks

        fun bind(status: ProjectsByStatus) = with(binding) {
            tvStatusTitle.text = status.title
            tvCount.text = status.items.size.toString()
            projectItemAdapter.submitList(status.items)
            tvEmpty.isVisible = status.items.isNullOrEmpty()
            rlAddProject.setOnClickListener {
                onAddProjectClick.invoke()
            }
            ivMenu.setOnClickListener {
                onMenuClick(Status(status.id, status.title), ivMenu)
            }
        }
    }

    inner class FooterVH(private val binding: FooterAddListBinding) :
        BaseVH(binding.root) {

        override fun getRecyclerView() = throw NullPointerException("There is not any RecyclerView")

        fun bind() = with(binding) {
            root.setOnClickListener {
                onAddColumnClick.invoke()
            }
        }
    }
}