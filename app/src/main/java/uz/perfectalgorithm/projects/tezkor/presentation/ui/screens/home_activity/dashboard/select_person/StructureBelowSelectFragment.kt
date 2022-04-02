package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentSelectStructureBelowBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person.DashboardStructureSelectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard.select_person.StructureSelectDashboardViewModel
import uz.perfectalgorithm.projects.tezkor.utils.adding.FragmentPageSelectedListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.StructureSelectDashboardListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.getCheckedPersonsDashboard
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultDataDashboard
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect

/**
 *Created by farrukh_kh on 10/1/21 10:47 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person
 **/
/**
 * Dashboard uchun strukturadan tanlash oynasi
 * tanlangan xodimni DashboardSelectPersonFragment ga uzatadi
 */
@AndroidEntryPoint
class StructureBelowSelectFragment : Fragment(), FragmentPageSelectedListener,
    StructureSelectDashboardListener {

    // TODO: 8/26/21 needs performance optimization
    private var _binding: FragmentSelectStructureBelowBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val viewModel by viewModels<StructureSelectDashboardViewModel>()
    private val departmentAdapter by lazy { DashboardStructureSelectAdapter(this) }
    private val selectionList = mutableSetOf<AllWorkersResponse.DataItem?>()

    private val storage by lazy { LocalStorage.instance }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectStructureBelowBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
    }

    private fun initViews() = with(binding) {
        rvDepartments.adapter = departmentAdapter
//        srlRefresh.setOnRefreshListener {
//            viewModel.initStaffBelowStructure()
//        }
    }

    private fun initObservers() {
        viewModel.structureBelow.simpleCollect(
            this,
            binding.pbLoading,
            binding.tvEmpty
        ) { departments ->
            departmentAdapter.submitList(departments)
//            binding.srlRefresh.isRefreshing = false
            binding.pbLoading.isVisible = false
            binding.tvEmpty.isVisible = departments.isNullOrEmpty()
            departments.getCheckedPersonsDashboard(storage.persons, selectionList)
            selectionList.firstOrNull()?.let {
                setResultDataDashboard(
                    StaffBelow(
                        it.id!!,
                        it.firstName ?: "",
                        it.lastName ?: ""
                    )
                )
            }
            departments.firstOrNull { it.id == storage.selectedDepartmentId }?.let {
                setResultDataDashboard(it)
            }
        }
    }

    override fun onDepartmentClick(department: DepartmentStructureBelow) {
        if (storage.selectedDepartmentId == department.id) {
            val value: DepartmentStructureBelow? = null
            setResultDataDashboard(value)
            storage.selectedDepartmentId = -1
        } else {
            setResultDataDashboard(department)
            storage.selectedDepartmentId = department.id
        }
        storage.persons = emptySet()
        selectionList.clear()
        departmentAdapter.notifyChanges()
//        department.getDepartmentWorkers().let { departmentWorkers ->
//            if (selectionList.containsAll(departmentWorkers)) {
//                selectionList.removeAll(departmentWorkers)
//            } else {
//                selectionList.addAll(departmentWorkers)
//            }
//        }
//        storage.persons = selectionList.map { it?.id.toString() }.toSet()
//        setResultList(storage.participant, selectionList.distinct().filterNotNull())
//        departmentAdapter.notifyChanges()
    }

    override fun onWorkerClick(worker: AllWorkersResponse.DataItem) {
        storage.selectedDepartmentId = -1
        if (selectionList.contains(worker)) {
            selectionList.remove(worker)
            val value: StaffBelow? = null
            setResultDataDashboard(value)
        } else {
            selectionList.clear()
            selectionList.add(worker)
            setResultDataDashboard(
                StaffBelow(
                    worker.id!!,
                    worker.firstName ?: "",
                    worker.lastName ?: ""
                )
            )
        }
        storage.persons = selectionList.map { it?.id.toString() }.toSet()
        departmentAdapter.notifyChanges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFragmentSelected() {
//        departmentAdapter.currentList.getCheckedPersonsDashboard(storage.persons, selectionList)
        departmentAdapter.notifyChanges()
    }
}