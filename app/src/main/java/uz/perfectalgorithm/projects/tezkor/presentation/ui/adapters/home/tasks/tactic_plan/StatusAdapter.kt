package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.tactic_plan

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanStatus
import uz.perfectalgorithm.projects.tezkor.databinding.FooterAddListBinding
import uz.perfectalgorithm.projects.tezkor.databinding.TacticPlanEntryBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.HorizontalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper.Companion.TYPE_CONTENT
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper.Companion.TYPE_FOOTER
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragColumn
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem
import kotlin.math.abs


class StatusAdapter(
    private val context: Context,
    private val onTacticPlanClick: SingleBlock<Int>,
    private val onAddTacticPlanClick: (Int, Int, Status) -> Unit,
    private val changeItemStatus: (TacticPlan, TacticPlanStatus) -> Unit
) : HorizontalAdapter<StatusAdapter.BaseVH>(context) {

    var yearId: Int? = null
    var monthId: Int? = null

    override fun getContentLayoutRes() = R.layout.tactic_plan_entry

    override fun getFooterLayoutRes() = R.layout.footer_add_list

    override fun onCreateViewHolder(parent: View?, viewType: Int) = when (viewType) {
        TYPE_CONTENT -> VH(
            TacticPlanEntryBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent as? ViewGroup,
                false
            )
        )
        TYPE_FOOTER -> FooterVH(
            FooterAddListBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent as? ViewGroup,
                false
            )
        )
        else -> throw IllegalArgumentException("There are only 2 viewTypes")
    }

    override fun onBindContentViewHolder(holder: BaseVH, dragColumn: DragColumn?, position: Int) {
        (holder as VH).bind(dragColumn as TacticPlanStatus)
    }

    override fun onBindFooterViewHolder(holder: BaseVH, position: Int) {
        (holder as FooterVH).bind()
    }

    override fun needFooter() = false

    abstract inner class BaseVH(itemView: View) :
        HorizontalAdapter<BaseVH>.ViewHolder(itemView)

    inner class VH(private val binding: TacticPlanEntryBinding) :
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
                    item as TacticPlan,
                    getItem(newPageIndex) as TacticPlanStatus
                )
            }
        }

        private val tacticPlanAdapter by lazy {
            TacticPlanAdapter(
                context,
                onTacticPlanClick,
                dragHelper!!,
                dragDropListener
            )
        }

        // TODO: 8/5/21 needs refactor
        init {
            binding.rvTacticPlans.apply {
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
                adapter = tacticPlanAdapter
            }
        }

        override fun getRecyclerView() = binding.rvTacticPlans

        fun bind(status: TacticPlanStatus) = with(binding) {
            tvStatusTitle.text = status.title
            tvCount.text = status.tacticPlans.size.toString()
            tacticPlanAdapter.submitList(status.tacticPlans)
            tvEmpty.isVisible = status.tacticPlans.isNullOrEmpty()
            rlAddTacticPlan.setOnClickListener {
                onAddTacticPlanClick.invoke(yearId!!, monthId!!, Status(status.id, status.title))
            }
        }
    }

    inner class FooterVH(private val binding: FooterAddListBinding) :
        BaseVH(binding.root) {

        override fun getRecyclerView() = throw NullPointerException("There is not any RecyclerView")

        fun bind() = with(binding) {
            // TODO: 8/3/21 show dialog
        }
    }
}