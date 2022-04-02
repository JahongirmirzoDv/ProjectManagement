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
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentAllStaffSelectBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.select_person.AllStaffSelectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person.AllStaffSelectViewModel
import uz.perfectalgorithm.projects.tezkor.utils.MASTER
import uz.perfectalgorithm.projects.tezkor.utils.PERFORMER
import uz.perfectalgorithm.projects.tezkor.utils.adding.FragmentPageSelectedListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultData
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultList
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh

/**
 *Created by farrukh_kh on 8/9/21 9:39 PM
 *uz.rdo.projects.projectmanagement.utils.adding
 **/
/**
 * Barcha xodimlar ro'yxatidan tanlash oynasi
 * tanlangan xodimni SelectPersonFragment ga uzatadi
 */
@AndroidEntryPoint
class AllStaffSelectFragment : Fragment(), FragmentPageSelectedListener {

    // TODO: 8/26/21 needs performance optimization
    private var _binding: FragmentAllStaffSelectBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val workersAdapter by lazy { AllStaffSelectAdapter(this::onWorkerClick) }

    private val viewModel by viewModels<AllStaffSelectViewModel>()

    private val selectionList = mutableSetOf<AllWorkersShort>()

    private val storage by lazy { LocalStorage.instance }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllStaffSelectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
        initViews()
    }

    private fun initObservers() {
        viewModel.workers.simpleCollectWithRefresh(this, binding.srlRefresh, binding.tvEmpty) { workers ->
            workersAdapter.submitList(workers.sortedBy { it.fullName?.trim()?.lowercase() })
            selectionList.clear()
            selectionList.addAll(workers.filter { storage.persons.contains(it.id.toString()) })
            binding.srlRefresh.isRefreshing = false
            binding.tvEmpty.isVisible = workers.isNullOrEmpty()
            if (storage.participant == PERFORMER || storage.participant == MASTER) {
                selectionList.firstOrNull()?.let { setResultData(storage.participant, it) }
            } else {
                setResultList(storage.participant, selectionList.distinct())
            }
        }
    }

    private fun initViews() = with(binding) {
        rvWorkers.adapter = workersAdapter
        srlRefresh.setOnRefreshListener { viewModel.initWorkers() }
    }

    override fun onFragmentSelected() {
        selectionList.clear()
        selectionList.addAll(workersAdapter.currentList.filter { storage.persons.contains(it.id.toString()) })
        workersAdapter.notifyDataSetChanged()
    }

    private fun onWorkerClick(worker: AllWorkersShort) {
        if (storage.participant == PERFORMER || storage.participant == MASTER) {
            if (selectionList.contains(worker)) {
                selectionList.remove(worker)
                setResultData(storage.participant, null)
            } else {
                selectionList.clear()
                selectionList.add(worker)
                setResultData(storage.participant, worker)
            }
            storage.persons = selectionList.map { it.id.toString() }.toSet()
        } else {
            if (selectionList.contains(worker)) {
                selectionList.remove(worker)
            } else {
                selectionList.add(worker)
            }
            storage.persons = selectionList.map { it.id.toString() }.toSet()
            setResultList(storage.participant, selectionList.distinct())
        }
        workersAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}