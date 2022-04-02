package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureDepartmentBinding
import uz.perfectalgorithm.projects.tezkor.utils.MASTER
import uz.perfectalgorithm.projects.tezkor.utils.PERFORMER
import uz.perfectalgorithm.projects.tezkor.utils.adding.StructureSelectListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.getDepartmentWorkers

/**
 *Created by farrukh_kh on 8/24/21 11:56 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.adapters.home.others.adding_helpers
 **/
class DepartmentSelectAdapter(private val clickListener: StructureSelectListener) :
    ListAdapter<DepartmentStructureShort, DepartmentSelectAdapter.VH>(ITEM_CALLBACK) {

    private var needsRefresh = false
    private val storage by lazy { LocalStorage.instance }

    // TODO: 8/25/21 optimization: remove notifyDataSetChanged()
    fun notifyChanges() {
        needsRefresh = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStructureDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemStructureDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val leaderAdapter by lazy {
            WorkerSelectAdapter(
                HierarchyPositionsEnum.LEADER,
                clickListener
            )
        }
        private val staffAdapter by lazy {
            WorkerSelectAdapter(
                HierarchyPositionsEnum.STAFF,
                clickListener
            )
        }
        private val childDepartmentAdapter by lazy { DepartmentSelectAdapter(clickListener) }

        init {
            binding.apply {
                rvLeaders.adapter = leaderAdapter
                rvStaff.adapter = staffAdapter
                rvDepartments.adapter = childDepartmentAdapter
                root.setOnClickListener {
                    rvLeaders.isVisible = !rvLeaders.isVisible
                    rvStaff.isVisible = !rvStaff.isVisible
                    rvDepartments.isVisible = !rvDepartments.isVisible
                    ivDropDown.setImageResource(
                        if (rvLeaders.isVisible) R.drawable.ic_chevron_down
                        else R.drawable.ic_chevron_up
                    )
                }
            }
        }

        fun bind(department: DepartmentStructureShort) = with(binding) {
            ivChecked.setOnClickListener {
                clickListener.onDepartmentClick(department)
            }
//            if (department.parent == null) {
//                rvLeaders.isVisible = true
//                rvStaff.isVisible = true
//                rvDepartments.isVisible = true
//                ivDropDown.setImageResource(R.drawable.ic_chevron_down)
//            }
            tvTitle.text = department.title
            val leaders = mutableListOf<AllWorkersShort?>()
            val staff = mutableListOf<AllWorkersShort?>()
            department.positions?.filter { it?.hierarchy == HierarchyPositionsEnum.LEADER.text }
                ?.forEach { position ->
                    position?.staffs?.let { leaders.addAll(it) }
                }
            department.positions?.filter { it?.hierarchy == HierarchyPositionsEnum.STAFF.text }
                ?.forEach { position ->
                    position?.staffs?.let { staff.addAll(it) }
                }
            leaderAdapter.submitList(leaders)
            staffAdapter.submitList(staff)
            childDepartmentAdapter.submitList(department.children)
            val departmentWorkers = department.getDepartmentWorkers()
            ivChecked.isVisible = storage.participant != PERFORMER
                    && storage.participant != MASTER
                    && !departmentWorkers.isNullOrEmpty()
            if (storage.persons.containsAll(departmentWorkers.map { it.id.toString() })) {
                ivChecked.setImageResource(R.drawable.custom_checkbox_checked)
            } else {
                ivChecked.setImageResource(R.drawable.custom_checkbox_unchecked)
            }
            if (needsRefresh) {
                leaderAdapter.notifyDataSetChanged()
                staffAdapter.notifyDataSetChanged()
                childDepartmentAdapter.notifyChanges()
                if (department == currentList.last()) {
                    needsRefresh = false
                }
            }
        }
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
}