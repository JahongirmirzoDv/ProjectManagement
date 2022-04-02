package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentSelectFavoritesBelowBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.select_person.DashboardStaffSelectAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard.select_person.FavoritesSelectDashboardViewModel
import uz.perfectalgorithm.projects.tezkor.utils.adding.FragmentPageSelectedListener
import uz.perfectalgorithm.projects.tezkor.utils.adding.setResultDataDashboard
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh

/**
 *Created by farrukh_kh on 10/1/21 10:46 PM
 *uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard.select_person
 **/
/**
 * Dashboard uchun sevimli xodimlar orasidan tanlash oynasi
 * tanlangan xodimni DashboardSelectPersonFragment ga uzatadi
 */
@AndroidEntryPoint
class FavoritesBelowSelectFragment : Fragment(), FragmentPageSelectedListener {

    // TODO: 8/26/21 needs performance optimization
    private var _binding: FragmentSelectFavoritesBelowBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException("View wasn't created")
    private val viewModel by viewModels<FavoritesSelectDashboardViewModel>()

    private val workersAdapter by lazy { DashboardStaffSelectAdapter(this::onWorkerClick) }
    private val selectionList = mutableSetOf<StaffBelow>()

    private val storage by lazy { LocalStorage.instance }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectFavoritesBelowBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
        initViews()
    }

    private fun initObservers() {
        viewModel.favoritesBelow.simpleCollectWithRefresh(
            this,
            binding.srlRefresh,
            binding.tvEmpty
        ) { favorites ->
            workersAdapter.submitList(favorites.sortedBy { it.firstName.trim().lowercase() })
            selectionList.clear()
            selectionList.addAll(favorites.filter { storage.persons.contains(it.id.toString()) })
            binding.srlRefresh.isRefreshing = false
            binding.tvEmpty.isVisible = favorites.isNullOrEmpty()
            selectionList.firstOrNull()?.let {
                setResultDataDashboard(it)
                storage.selectedDepartmentId = -1
            }
        }
    }

    private fun initViews() = with(binding) {
        rvWorkers.adapter = workersAdapter
        srlRefresh.setOnRefreshListener { viewModel.initFavoritesBelow() }
    }

    override fun onFragmentSelected() {
        selectionList.clear()
        selectionList.addAll(workersAdapter.currentList.filter { storage.persons.contains(it.id.toString()) })
        workersAdapter.notifyDataSetChanged()
    }

    private fun onWorkerClick(worker: StaffBelow) {
        if (selectionList.contains(worker)) {
            selectionList.remove(worker)
            val value: StaffBelow? = null
            setResultDataDashboard(value)
        } else {
            selectionList.clear()
            selectionList.add(worker)
            setResultDataDashboard(worker)
        }
        storage.persons = selectionList.map { it.id.toString() }.toSet()
        storage.selectedDepartmentId = -1
        workersAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}