package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.select_person

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentStructureSelectBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person.DepartmentSelectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person.StructureSelectViewModel
import uz.perfectalgorithm.projects.tezkor.utils.MASTER
import uz.perfectalgorithm.projects.tezkor.utils.PERFORMER
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect

/**
 *Created by farrukh_kh on 8/24/21 10:57 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.others.adding_helpers
 **/
/**
 * Strukturadan tanlash oynasi
 * tanlangan xodimni SelectPersonFragment ga uzatadi
 */
@AndroidEntryPoint
class StructureSelectFragment : Fragment(), FragmentPageSelectedListener, StructureSelectListener {

    // TODO: 8/26/21 needs performance optimization
    private var _binding: FragmentStructureSelectBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val viewModel by viewModels<StructureSelectViewModel>()
    private val departmentAdapter by lazy { DepartmentSelectAdapter(this) }
    private val selectionList = mutableSetOf<AllWorkersShort?>()

    private val storage by lazy { LocalStorage.instance }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStructureSelectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
    }

    private fun initViews() = with(binding) {
        rvDepartments.adapter = departmentAdapter
//        srlRefresh.setOnRefreshListener {
//            viewModel.initStructure()
//        }
    }

    private fun initObservers() {
        viewModel.structure.simpleCollect(this, binding.pbLoading, binding.tvEmpty) { departments ->
            departmentAdapter.submitList(departments)
//            binding.srlRefresh.isRefreshing = false
            binding.pbLoading.isVisible = false
            binding.tvEmpty.isVisible = departments.isNullOrEmpty()
            departments.getCheckedPersonsShort(storage.persons, selectionList)
            if (storage.participant == PERFORMER || storage.participant == MASTER) {
                selectionList.firstOrNull()?.let { setResultData(storage.participant, it) }
            } else {
                setResultList(storage.participant, selectionList.distinct().filterNotNull())
            }
        }
    }

    override fun onDepartmentClick(department: DepartmentStructureShort) {
        department.getDepartmentWorkers().let { departmentWorkers ->
            if (selectionList.containsAll(departmentWorkers)) {
                selectionList.removeAll(departmentWorkers)
            } else {
                selectionList.addAll(departmentWorkers)
            }
        }
        storage.persons = selectionList.map { it?.id.toString() }.toSet()
        setResultList(storage.participant, selectionList.distinct().filterNotNull())
        departmentAdapter.notifyChanges()
    }

    override fun onWorkerClick(worker: AllWorkersShort) {
        if (storage.participant == PERFORMER || storage.participant == MASTER) {
            if (selectionList.contains(worker)) {
                selectionList.remove(worker)
                setResultData(storage.participant, null)
            } else {
                selectionList.clear()
                selectionList.add(worker)
                setResultData(storage.participant, worker)
            }
            storage.persons = selectionList.map { it?.id.toString() }.toSet()
        } else {
            if (selectionList.contains(worker)) {
                selectionList.remove(worker)
            } else {
                selectionList.add(worker)
            }
            storage.persons = selectionList.map { it?.id.toString() }.toSet()
            setResultList(storage.participant, selectionList.distinct().filterNotNull())
        }
        departmentAdapter.notifyChanges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFragmentSelected() {
        departmentAdapter.currentList.getCheckedPersonsShort(storage.persons, selectionList)
        departmentAdapter.notifyChanges()
    }
}