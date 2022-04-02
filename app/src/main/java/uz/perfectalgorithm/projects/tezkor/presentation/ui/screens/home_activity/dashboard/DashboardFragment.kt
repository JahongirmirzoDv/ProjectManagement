package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.*
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DashboardGoal
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDashboardBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.DashboardChartAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.DashboardCountAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard.DashboardViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.showDashboardGoalDialog
import uz.perfectalgorithm.projects.tezkor.utils.adding.showSelectPersonFragmentDashboard
import uz.perfectalgorithm.projects.tezkor.utils.adding.toUiDate
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.showDateRangePicker
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.toChartData
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.toCountData
import uz.perfectalgorithm.projects.tezkor.utils.dashboard.toUiDateRange
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollectWithRefresh
import java.util.*
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/13/21 3:47 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.dashboard
 **/
/**
 * Others dan Dashboardga kirgandagi oyna
 * TODO:
 * 1. Biror xodim (bo'lim) tanlanganda, 2ta request ketyapti.
 * shu sababli eski tanlangan xodim ma'lumotlari UIga set bo'lib qolyotgan
 * case lar bor. Tavsiya: StateFlow ni SharedFlow ga o'tkazish yoki SingleLiveEventObserver
 * ishlatish
 * 2. UI (RecyclerView) optimizatsiya qilish (biroz freeze (qotish) bor)
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val dashboardViewModel by viewModels<DashboardViewModel>()
    private val chartAdapter by lazy { DashboardChartAdapter() }
    private val countAdapter by lazy { DashboardCountAdapter() }
    private var dateRange = Pair(LocalDate().withDayOfMonth(1), LocalDate())
    private var selectedStaff: StaffBelow? = null
    private var selectedDepartment: DepartmentStructureBelow? = null
    private var selectedGoal: DashboardGoal? = null
    private val goals = mutableListOf<DashboardGoal>()

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAppBar()
        showBottomMenu()

        if (selectedStaff == null && selectedDepartment == null) {
            dashboardViewModel.initDashboardGoals(storage.userId, null)
            selectedStaff = StaffBelow(storage.userId, "Shaxsiy", "")
        }
        setupViews()
        setupObservers()
        dashboardViewModel.initStaffBelow()
        if (binding.tvGoal.text.isNullOrBlank()) {
            if (selectedStaff != null || selectedDepartment != null) {
                dashboardViewModel.initDashboardGoals(selectedStaff?.id, selectedDepartment?.id)
            } else {
                makeErrorSnack("Xodim yoki bo'limni tanlang")
                binding.tvPerson.text = ""
            }
        }
    }

    private fun setupViews() = with(binding) {
        tvPerson.text = selectedStaff?.toString()
        tvPerson.isSelected = true
        tvDate.apply {
            text = dateRange.toUiDateRange()
            tvDate.isSelected = true
        }
        binding.rvCharts.adapter = chartAdapter
        rvCounts.adapter = countAdapter
        srlRefresh.setOnRefreshListener {
            initStatistics()
            dashboardViewModel.initStaffBelow()
            if (selectedStaff != null || selectedDepartment != null) {
                dashboardViewModel.initDashboardGoals(selectedStaff?.id, selectedDepartment?.id)
            } else {
                makeErrorSnack("Xodim yoki bo'limni tanlang")
            }
        }
        llDate.setOnClickListener {
            showDateRangePicker(dateRange, this@DashboardFragment::setDateRange)
        }
        llPerson.setOnClickListener {
            storage.participant = DASHBOARD_STAFF
            if (selectedStaff == null) {
                storage.persons = emptySet()
            } else {
                storage.persons = setOf(selectedStaff!!.id.toString())
            }
            if (selectedDepartment == null) {
                storage.selectedDepartmentId = -1
            } else {
                storage.selectedDepartmentId = selectedDepartment!!.id
            }
            showSelectPersonFragmentDashboard()
        }
        llGoal.setOnClickListener {
            if (goals.isNullOrEmpty()) {
                makeErrorSnack("Maqsaadlar ro'yxati yuklanmagan")
            } else {
                showDashboardGoalDialog(selectedGoal, goals) {
                    selectedGoal = it
                    tvGoal.text = it.title.trim()
                    tvGoal.isSelected = true
                    initStatistics()
                }
            }
        }
    }

    private fun setupObservers() = with(dashboardViewModel) {
        staffBelow.simpleCollectWithRefresh(this@DashboardFragment, null) { data ->
            binding.llPerson.isVisible = !data.isNullOrEmpty()
        }

        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<StaffBelow?>(DASHBOARD_STAFF)
                ?.observe(viewLifecycleOwner) {
                    selectedStaff = it
                    if (it != null) {
                        binding.tvPerson.text = it.toString()
                        selectedDepartment = null
                        dashboardViewModel.initDashboardGoals(
                            selectedStaff?.id,
                            selectedDepartment?.id
                        )
                    }
                }

            this?.getLiveData<DepartmentStructureBelow?>(DASHBOARD_DEPARTMENT)
                ?.observe(viewLifecycleOwner) {
                    selectedDepartment = it
                    if (it != null) {
                        binding.tvPerson.text = it.title
                        selectedStaff = null
                        dashboardViewModel.initDashboardGoals(
                            selectedStaff?.id,
                            selectedDepartment?.id
                        )
                    }
                }
        }

        dashboardGoals.simpleCollectWithRefresh(
            this@DashboardFragment,
            binding.srlRefresh
        ) { data ->
            val list = data.toMutableList()
            list.add(0, DashboardGoal(-1, "Barcha maqsadlar"))
            goals.clear()
            goals.addAll(list)
            if (selectedGoal == null || !goals.contains(selectedGoal)) {
                selectedGoal = list.first()
            }
            binding.tvGoal.text = selectedGoal!!.title.trim()
            binding.tvGoal.isSelected = true
            initStatistics()
        }

        statistics.simpleCollectWithRefresh(this@DashboardFragment,
            srlRefresh = binding.srlRefresh,
            tvError = binding.tvEmpty,
            onError = {
                countAdapter.submitList(emptyList())
                chartAdapter.submitList(emptyList())
            },
            onSuccess = { data ->
                countAdapter.submitList(data.toCountData())
                chartAdapter.submitList(data.toChartData())
                binding.srlRefresh.isRefreshing = false
            })
    }

    private fun initStatistics() {
        if (selectedGoal?.id == -1) {
            if (selectedStaff == null && selectedDepartment == null) {
                makeErrorSnack("Xodim yoki bo'limni tanlang")
            } else {
                dashboardViewModel.initStatistics(
                    selectedStaff?.id,
                    selectedDepartment?.id,
                    null,
                    dateRange.first.toUiDate(),
                    dateRange.second.toUiDate()
                )
            }
        } else {
            if (selectedStaff == null && selectedDepartment == null) {
                makeErrorSnack("Xodim yoki bo'limni tanlang")
            } else {
                dashboardViewModel.initStatistics(
                    selectedStaff?.id,
                    selectedDepartment?.id,
                    selectedGoal?.id,
                    dateRange.first.toUiDate(),
                    dateRange.second.toUiDate()
                )
            }
        }
    }

    private fun setDateRange(selections: Pair<LocalDate, LocalDate>) {
        dateRange = selections
        binding.tvDate.apply {
            text = dateRange.toUiDateRange()
            isSelected = true
        }
        initStatistics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}