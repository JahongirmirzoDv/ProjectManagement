package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure_new

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemDepartmentBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

/**
 *Created by farrukh_kh on 11/1/21 8:31 AM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers.structure_new
 **/
class DepartmentAdapter(
    private val onDeleteClick: SingleBlock<Int>,
    private val onEditClick: DoubleBlock<String, Int>,
    private val onWorkerMenuClick: DoubleBlock<AllWorkersShort, Int>,
    private val onWorkerClick: DoubleBlock<AllWorkersShort, Int>,
    private var linear: Boolean = false
) : RecyclerView.Adapter<DepartmentAdapter.VH>() {
    private var list = ArrayList<DepartmentStructureShort>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            val department = list[position]
            val leaderAdapter = WorkerAdapter(HierarchyPositionsEnum.LEADER, onWorkerMenuClick, onWorkerClick)
            val staffAdapter = WorkerAdapter(HierarchyPositionsEnum.STAFF, onWorkerMenuClick, onWorkerClick)
            val childDepartmentAdapter = DepartmentAdapter(onDeleteClick, onEditClick, onWorkerMenuClick, onWorkerClick)

                rvLeaders.adapter = leaderAdapter
                rvWorkers.adapter = staffAdapter
                rvSection.adapter = childDepartmentAdapter
                rootLayout.setOnClickListener { holder.setVisibility() }
                rootLayout.setOnLongClickListener {
                    holder.setChangeVisibility()
                    return@setOnLongClickListener true
                }

            txtSectionTitle.text = department.title
            val leaders = ArrayList<AllWorkersShort>()
            val staff = ArrayList<AllWorkersShort>()
            department.positions?.filter { it?.hierarchy == HierarchyPositionsEnum.LEADER.text }
                ?.forEach { position ->
                    position?.staffs?.let {
                        leaders.addAll(it) }
                }
            department.positions?.filter { it?.hierarchy == HierarchyPositionsEnum.STAFF.text }
                ?.forEach { position ->
                    position?.staffs?.let {
                        staff.addAll(it) }
                }

            if (linear) {
                holder.setVisibilityByBoolean()
                childDepartmentAdapter.isOpen(true)
            } else {
                holder.setVisibilityByBoolean()
                childDepartmentAdapter.isOpen(false)
            }

            leaderAdapter.submitList(leaders)
            staffAdapter.submitList(staff)
            childDepartmentAdapter.submitList(department.children)
            ivDelete.setOnClickListener {
                department.id?.let { it1 -> onDeleteClick.invoke(it1) }
            }
            ivEdit.setOnClickListener {
                department.id?.let { it1 -> onEditClick.invoke(department.title ?: "", it1) }
            }
            imgDropDown.setOnClickListener {
                holder.setVisibility()
            }
        }
    }

    fun isOpen(a: Boolean) {
        linear = a
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemDepartmentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setChangeVisibility() {
            binding.ivDelete.isVisible = !binding.ivDelete.isVisible
            binding.ivEdit.isVisible = !binding.ivEdit.isVisible
        }

        fun setVisibility() {
            binding.apply {
                rvLeaders.isVisible = !rvLeaders.isVisible
                rvWorkers.isVisible = !rvWorkers.isVisible
                rvSection.isVisible = !rvSection.isVisible

                imgDropDown.setImageResource(
                    if (rvLeaders.isVisible) R.drawable.ic_chevron_down
                    else R.drawable.ic_chevron_up
                )
            }
        }

        fun setVisibilityByBoolean() {
            binding.apply {
                if (linear) {
                    rvLeaders.visibility = View.VISIBLE
                    rvWorkers.visibility = View.VISIBLE
                    rvSection.visibility = View.VISIBLE
                } else {
                    rvLeaders.visibility = View.GONE
                    rvWorkers.visibility = View.GONE
                    rvSection.visibility = View.GONE
                }
                imgDropDown.setImageResource(
                    if (linear) R.drawable.ic_chevron_down
                    else R.drawable.ic_chevron_up
                )
            }
        }
    }

    fun submitList(listW: List<DepartmentStructureShort>?) {
        if (!listW.isNullOrEmpty()) {
            list.clear()
            list.addAll(listW)
            notifyDataSetChanged()
        }
    }

    fun changeUI(worker: AllWorkersShort) {
//        if (leaderAdapter.icsPosition>-1) {
//            leaderAdapter.setUIAddFavourite(worker)
//            Log.d("Department", "changeUI: leader")
//        } else {
//            Log.d("Department", "changeUI: staff${staffAdapter.icsPosition}")
//            staffAdapter.setUIAddFavourite(worker)
//        }
    }

    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DepartmentStructureShort>() {
            override fun areItemsTheSame(
                oldItem: DepartmentStructureShort,
                newItem: DepartmentStructureShort
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DepartmentStructureShort,
                newItem: DepartmentStructureShort
            ) = oldItem == newItem
        }
    }

    override fun getItemCount(): Int = list.size
}