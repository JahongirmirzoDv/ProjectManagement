package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.tactic_plan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlan
import uz.perfectalgorithm.projects.tezkor.databinding.TacticPlanItemBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.adapter.VerticalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.callback.DragDropListener
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.helper.DragHelper
import uz.perfectalgorithm.projects.tezkor.utils.views.drag_board_view.model.DragItem


class TacticPlanAdapter(
    context: Context,
    private val onTacticPlanClick: SingleBlock<Int>,
    dragHelper: DragHelper,
    dragDropListener: DragDropListener
) : VerticalAdapter<TacticPlanAdapter.VH>(context, dragHelper, dragDropListener) {

    inner class VH(private val binding: TacticPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tacticPlan: TacticPlan) = with(binding) {
            root.setOnLongClickListener {
                dragItem(this@VH)
                true
            }
            root.setOnClickListener {
                onTacticPlanClick(tacticPlan.id)
            }
            tvTitle.text = tacticPlan.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        TacticPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(context: Context?, holder: VH, item: DragItem, position: Int) {
        holder.bind(item as TacticPlan)
    }
}