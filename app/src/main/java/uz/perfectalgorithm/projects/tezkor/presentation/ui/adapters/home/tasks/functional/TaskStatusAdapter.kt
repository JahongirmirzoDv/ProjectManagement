package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.functional

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TasksByStatus
import uz.perfectalgorithm.projects.tezkor.databinding.FooterAddListBinding
import uz.perfectalgorithm.projects.tezkor.databinding.TaskEntryBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.ThreeBlock
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.HorizontalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem
import kotlin.math.abs


/**
 *Created by farrukh_kh on 8/5/21 2:28 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.tasks.functional
 **/
class TaskStatusAdapter(
    private val context: Context,
    private val onAddNewTask: ThreeBlock<String, Int, Int>,
    private val onAddColumnClick: EmptyBlock,
    private val changeItemStatus: DoubleBlock<FolderItem, TasksByStatus>,
    private val onTaskClick: SingleBlock<Int>,
    private val onMenuClick: DoubleBlock<Status, View>
) : HorizontalAdapter<TaskStatusAdapter.BaseVH>(context) {

    var folderId: Int? = null

    override fun getContentLayoutRes() = R.layout.task_entry

    override fun getFooterLayoutRes() = R.layout.footer_add_list

    override fun onCreateViewHolder(parent: View?, viewType: Int) = when (viewType) {
        DragHelper.TYPE_CONTENT -> VH(
            TaskEntryBinding.inflate(
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
        (holder as VH).bind(dragColumn as TasksByStatus)
    }

    override fun onBindFooterViewHolder(holder: BaseVH, position: Int) {
        (holder as FooterVH).bind()
    }

    override fun needFooter() = true

    abstract inner class BaseVH(itemView: View) :
        HorizontalAdapter<BaseVH>.ViewHolder(itemView)

    inner class VH(private val binding: TaskEntryBinding) :
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
                    item as FolderItem,
                    getItem(newPageIndex) as TasksByStatus
                )
            }
        }

        private val taskAdapter by lazy {
            TaskAdapter(
                context,
                dragHelper!!,
                dragDropListener,
                onTaskClick
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
                adapter = taskAdapter
            }
        }

        override fun getRecyclerView() = binding.rvTasks

        @SuppressLint("ClickableViewAccessibility")
        fun bind(status: TasksByStatus) = with(binding) {
            touchOutside.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (mcvAddTask.isVisible) {
                        val outRect = Rect()
                        mcvAddTask.getGlobalVisibleRect(outRect)
                        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                            etNewTask.setText("")
                            root.context.hideKeyboard(etNewTask)
                            mcvAddTask.isVisible = false
                            rlAddTask.isVisible = true
                        }
                    }
                }
                false
            }
            tvStatusTitle.text = status.title
            tvCount.text = status.items.size.toString()
            taskAdapter.submitList(status.items)
            tvEmpty.isVisible = status.items.isNullOrEmpty()
            rlAddTask.setOnClickListener {
                rlAddTask.isVisible = false
                mcvAddTask.isVisible = true
                (status.items.size - 1).let {
                    rvTasks.smoothScrollToPosition(if (it < 0) 0 else it)
                }
            }
            ivAdd.setOnClickListener {
                if (etNewTask.text.isNullOrBlank()) {
                    etNewTask.error = "Vazifa nomini kiriting"
                } else {
                    onAddNewTask(etNewTask.text.toString().trim(), folderId ?: 0, status.id)
                    etNewTask.setText("")
                }
            }
            etNewTask.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etNewTask.text.isNullOrBlank()) {
                        etNewTask.error = "Vazifa nomini kiriting"
                    } else {
                        onAddNewTask(etNewTask.text.toString().trim(), folderId ?: 0, status.id)
                        etNewTask.setText("")
                    }
                    handled = true
                }
                handled
            }
            ivCancel.setOnClickListener {
                etNewTask.setText("")
                rlAddTask.isVisible = true
                mcvAddTask.isVisible = false
            }
            ivMenu.setOnClickListener {
                onMenuClick(Status(status.id, status.title, status.isViewable), ivMenu)
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