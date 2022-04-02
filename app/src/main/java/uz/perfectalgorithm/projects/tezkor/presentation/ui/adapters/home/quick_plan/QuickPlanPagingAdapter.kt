package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_plan

import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.TransitionStatusEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.databinding.ItemQuickPlanBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_plans.QuickPlansFragment
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.dragdrop_views.attachViewDragListener
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.views.textviewutils.setDefaultFlag
import uz.perfectalgorithm.projects.tezkor.utils.views.textviewutils.setStrikeFlag
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 *Created by farrukh_kh on 8/21/21 4:54 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.quick_plan
 **/

class QuickPlanPagingAdapter(
    private val onAddChildQuickPlan: SingleBlock<CreateQuickPlanRequest>,
    private val onChangeStatus: DoubleBlock<Int, Boolean>,
    private val onEditClick: SingleBlock<Int>,
    private val onVisibleMainETListener: SingleBlock<Boolean>,
    private val onDropItemListener: DoubleBlock<String, String>,
) : PagingDataAdapter<QuickPlan, QuickPlanPagingAdapter.VH>(ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemQuickPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.binding.chbIsDone.apply {
            setOnCheckedChangeListener(null)
            isChecked = false
        }
    }

    inner class VH(val binding: ItemQuickPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val childAdapter by lazy {
            QuickPlanAdapter(
                onAddChildQuickPlan,
                onChangeStatus,
                onEditClick,
                onVisibleMainETListener,
                onDropItemListener
            )
        }

        init {
            binding.rvChildren.adapter = childAdapter
        }

        fun bind(quickPlan: QuickPlan?) = with(binding) {
            tvTitle.text = quickPlan?.title
            chbIsDone.isChecked = quickPlan?.isDone == true
            quickPlan?.isDone?.let { setPlanToDone(it) }
            chbIsDone.setOnCheckedChangeListener { _, isChecked ->
                quickPlan?.id?.let { onChangeStatus(it, isChecked) }
            }
            cv1Root.setOnClickListener {
                quickPlan?.id?.let { id ->
                    onEditClick(id)
                }
            }
            childAdapter.parent = quickPlan
            childAdapter.submitList(quickPlan?.children)
            ivCreate.setOnClickListener {
                setVisibilityET()
                if (etQuickPlan.text.isNullOrBlank()) {
                    Toast.makeText(
                        root.context,
                        root.context.getString(R.string.empty_plan_field_toast_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onAddChildQuickPlan(
                        CreateQuickPlanRequest(
                            etQuickPlan.text.toString(),
                            quickPlan!!.date,
                            quickPlan.id
                        )
                    )
                    etQuickPlan.setText("")
                }
            }

            btnAddChild.setOnClickListener {
                setVisibilityET()
            }

            cv1Root.setOnLongClickListener {
                QuickPlansFragment.draggedItemID = getItem(absoluteAdapterPosition)!!.id
                attachViewDragListener(cv1Root)
                true
            }

            cv1Root.setOnDragListener(cardViewDragListener)
            lineSpace.setOnDragListener(lineSpaceDragListener)

        }

        private fun setVisibilityET() {
            binding.apply {
                if (btnAddChild.rotation == 0F) {
                    llCreate.visible()
                    etQuickPlan.isFocusable = true
                    btnAddChild.rotation = 45F
                    onVisibleMainETListener(false)
                } else {
                    btnAddChild.rotation = 0F
                    llCreate.gone()
                    onVisibleMainETListener(true)
                }
            }
        }

        private val cardViewDragListener = View.OnDragListener { view, dragEvent ->

            val draggableItem = dragEvent.localState as View

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // dims the view when the mask has entered the drop area

                    binding.clRoot.setBackgroundResource(R.color.cv_active_color)
                    if (getItem(absoluteAdapterPosition) != null) {
                        QuickPlansFragment.newParentItemID = getItem(absoluteAdapterPosition)!!.id
                    }
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    binding.clRoot.setBackgroundResource(R.color.colorWhiteSoft)
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    try {
                        binding.clRoot.setBackgroundResource(R.color.colorWhiteSoft)
                        onDropItemListener(
                            TransitionStatusEnum.INTO_PARENT.text,
                            TransitionStatusEnum.INTO_PARENT.text
                        )
                    } catch (e: Exception) {
                        myLogD("${e.message.toString()}")
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    true
                }
                else -> {
                    false
                }
            }
        }

        private val lineSpaceDragListener = View.OnDragListener { view, dragEvent ->

            val draggableItem = dragEvent.localState as View

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    binding.lineSpace.setBackgroundResource(R.color.yellow)
                    if (getItem(absoluteAdapterPosition) != null) {
                        if (absoluteAdapterPosition < itemCount - 1) {
                            QuickPlansFragment.upperItemPosition =
                                getItem(absoluteAdapterPosition)!!?.position
                            QuickPlansFragment.lowerItemPosition =
                                getItem(absoluteAdapterPosition + 1)?.position
                        } else {
                            QuickPlansFragment.upperItemPosition =
                                getItem(absoluteAdapterPosition)!!?.position
                            QuickPlansFragment.lowerItemPosition = null
                        }
                    }
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    binding.lineSpace.setBackgroundResource(R.color.white)
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    try {
                        binding.lineSpace.setBackgroundResource(R.color.white)
                        onDropItemListener(
                            TransitionStatusEnum.INTO_POSITION.text,
                            TransitionStatusEnum.INTO_POSITION.text
                        )
                    } catch (e: Exception) {
                        myLogD("${e.message.toString()}")
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    true
                }
                else -> {
                    false
                }
            }
        }

        private fun setPlanToDone(isDone: Boolean) {
            if (isDone) {
                binding.tvTitle.setStrikeFlag()
            } else {
                binding.tvTitle.setDefaultFlag()
            }
        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<QuickPlan>() {
            override fun areItemsTheSame(oldItem: QuickPlan, newItem: QuickPlan) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: QuickPlan, newItem: QuickPlan) =
                oldItem == newItem

            override fun getChangePayload(oldItem: QuickPlan, newItem: QuickPlan) = false
        }
    }
}