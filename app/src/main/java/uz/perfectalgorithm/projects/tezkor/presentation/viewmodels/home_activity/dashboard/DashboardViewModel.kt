package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DashboardGoal
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DashboardStatistics
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.domain.home.dashboard.DashboardRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/13/21 4:28 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.dashboard
 **/
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {

    private val _staffBelow = MutableStateFlowWrapper<List<StaffBelow>>()
    val staffBelow: StateFlowWrapper<List<StaffBelow>> = _staffBelow.asStateFlow()

    private val _dashboardGoals = MutableStateFlowWrapper<List<DashboardGoal>>()
    val dashboardGoals: StateFlowWrapper<List<DashboardGoal>> = _dashboardGoals.asStateFlow()

    private val _statistics = MutableStateFlowWrapper<DashboardStatistics>()
    val statistics: StateFlowWrapper<DashboardStatistics> = _statistics.asStateFlow()

    fun initDashboardGoals(staffId: Int?, departmentId: Int?) =
        viewModelScope.launch(Dispatchers.IO) {
            _dashboardGoals.value = DataWrapper.Loading()
            _dashboardGoals.value = dashboardRepository.getDashboardGoals(staffId, departmentId)
        }

    fun initStaffBelow() = viewModelScope.launch(Dispatchers.IO) {
        _staffBelow.value = DataWrapper.Loading()
        _staffBelow.value = dashboardRepository.getStaffBelow()
    }

    fun initStatistics(
        staffId: Int?,
        departmentId: Int?,
        goalId: Int?,
        startDate: String,
        endDate: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _statistics.value = DataWrapper.Loading()
        _statistics.value =
            dashboardRepository.getStatistics(
                staffId,
                departmentId,
                goalId,
                startDate,
                endDate
            )
    }
}