package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_plan

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
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
 *Created by farrukh_kh on 8/18/21 12:44 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.quick_plan
 **/


class QuickPlanAdapter(
    private val onAddChildQuickPlan: SingleBlock<CreateQuickPlanRequest>,
    private val onChangeStatus: DoubleBlock<Int, Boolean>,
    private val onEditClick: SingleBlock<Int>,
    private val onVisibleMainETListener: SingleBlock<Boolean>,
    private val onDropItemListener: DoubleBlock<String, String>
) : ListAdapter<QuickPlan, QuickPlanAdapter.VH>(ITEM_CALLBACK) {

    var parent: QuickPlan? = null

    private var listenDragDropItem: SingleBlock<QuickPlan>? = null
    private var listenBelowDragItem: SingleBlock<QuickPlan>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemQuickPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)

        holder.binding.chbIsDone.setOnCheckedChangeListener(null)
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

        @SuppressLint("ResourceAsColor")
        fun bind(quickPlan: QuickPlan) = with(binding) {
            tvTitle.text = quickPlan.title
            chbIsDone.isChecked = quickPlan.isDone
            setPlanToDone(quickPlan.isDone)
            chbIsDone.setOnCheckedChangeListener { _, isChecked ->
                onChangeStatus(quickPlan.id, isChecked)
            }
            if (quickPlan.isTask) {
                imgIsTask.visibility = View.VISIBLE
            } else {
                imgIsTask.visibility = View.GONE
            }
            cv1Root.setOnClickListener {
                if (!quickPlan.isTask) {
                    onEditClick(quickPlan.id)
                }
            }
            childAdapter.parent = quickPlan
            childAdapter.submitList(quickPlan.children)
            ivCreate.setOnClickListener {
                setVisibilityET()
                if (etQuickPlan.text.isNullOrBlank()) {
                    Toast.makeText(root.context, "Reja matnini kiriting", Toast.LENGTH_SHORT).show()
                } else {
                    onAddChildQuickPlan(
                        CreateQuickPlanRequest(
                            etQuickPlan.text.toString(),
                            quickPlan.date,
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
                QuickPlansFragment.draggedItemID = getItem(absoluteAdapterPosition).id
                attachViewDragListener(cv1Root)
                true
            }

            cv1Root.setOnDragListener(cardViewDragListener)
            lineSpace.setOnDragListener(lineSpaceDragListener)

            if (parent != null) {
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.height = 5
                lineSpace.layoutParams = layoutParams
                binding.lineSpace.setBackgroundResource(R.color.colorWhiteSoft)
            }
        }

        private fun setVisibilityET() {
            binding.apply {
                if (btnAddChild.rotation == 0F) {
                    llCreate.visible()
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
                        QuickPlansFragment.newParentItemID = getItem(absoluteAdapterPosition).id
                    }

                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {

                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // reset opacity if the mask exits drop area without drop action
                    //when mask exits drop-area without dropping set mask visibility to VISIBLE

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
                    if (parent != null) {
                        binding.lineSpace.setBackgroundResource(R.color.colorWhiteSoft)
                    } else {
                        binding.lineSpace.setBackgroundResource(R.color.white)
                    }
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    try {
                        if (parent != null) {
                            binding.lineSpace.setBackgroundResource(R.color.colorWhiteSoft)
                        } else {
                            binding.lineSpace.setBackgroundResource(R.color.white)

                        }
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
                oldItem == newItem && oldItem.children.toTypedArray()
                    .contentEquals(newItem.children.toTypedArray())

            override fun getChangePayload(oldItem: QuickPlan, newItem: QuickPlan) = false
        }
    }

}