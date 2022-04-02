package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.HierarchyPositionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStructureDepartmentBinding
import uz.perfectalgorithm.projects.tezkor.utils.adding.StructureSelectDashboardListener

/**
 *Created by farrukh_kh on 10/1/21 11:53 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person
 **/
class DashboardStructureSelectAdapter(private val clickListener: StructureSelectDashboardListener) :
    ListAdapter<DepartmentStructureBelow, DashboardStructureSelectAdapter.VH>(ITEM_CALLBACK) {

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
            DashboardStructureInnerAdapter(
                HierarchyPositionsEnum.LEADER,
                clickListener
            )
        }
        private val staffAdapter by lazy {
            DashboardStructureInnerAdapter(
                HierarchyPositionsEnum.STAFF,
                clickListener
            )
        }
        private val childDepartmentAdapter by lazy { DashboardStructureSelectAdapter(clickListener) }

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

        fun bind(department: DepartmentStructureBelow) = with(binding) {
            ivChecked.setOnClickListener {
                clickListener.onDepartmentClick(department)
            }
            tvTitle.text = department.title
            val leaders = mutableListOf<AllWorkersResponse.DataItem?>()
            val staff = mutableListOf<AllWorkersResponse.DataItem?>()
            department.positions?.filter { it.hierarchy == HierarchyPositionsEnum.LEADER.text }
                ?.forEach { position ->
                    position.staffs?.let { leaders.addAll(it) }
                }
            department.positions?.filter { it.hierarchy == HierarchyPositionsEnum.STAFF.text }
                ?.forEach { position ->
                    position.staffs?.let { staff.addAll(it) }
                }
            leaderAdapter.submitList(leaders)
            staffAdapter.submitList(staff)
            childDepartmentAdapter.submitList(department.children)
//            ivChecked.isVisible = department.myRole == HierarchyPositionsEnum.LEADER.text ||
//                    storage.role == RoleEnum.OWNER.text
            ivChecked.setImageResource(
                if (storage.selectedDepartmentId == department.id)
                    R.drawable.custom_checkbox_checked else R.drawable.custom_checkbox_unchecked
            )
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
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DepartmentStructureBelow>() {
            override fun areItemsTheSame(
                oldItem: DepartmentStructureBelow,
                newItem: DepartmentStructureBelow
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DepartmentStructureBelow,
                newItem: DepartmentStructureBelow
            ) = oldItem == newItem
        }
    }
}