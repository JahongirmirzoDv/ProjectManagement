package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.CreateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.CreateTacticPlanResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan.TacticPlanRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/29/21 2:32 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.tactic_plan
 **/
@HiltViewModel
class CreateTacticPlanViewModel @Inject constructor(
    private val tacticPlanRepository: TacticPlanRepository
) : ViewModel() {

    private val _statusList =
        MutableStateFlow<DataWrapper<List<Status>>>(DataWrapper.Empty())
    val statusList: StateFlow<DataWrapper<List<Status>>> get() = _statusList

    private val _response =
        MutableStateFlow<DataWrapper<CreateTacticPlanResponse>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<CreateTacticPlanResponse>> get() = _response

    init {
        initStatusList()
    }

    fun createTacticPlan(createTacticPlanRequest: CreateTacticPlanRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = tacticPlanRepository.createTacticPlan(createTacticPlanRequest)
        }

    fun initStatusList() = viewModelScope.launch(Dispatchers.IO) {
        _statusList.value = DataWrapper.Loading()
        _statusList.value = tacticPlanRepository.getTacticPlanStatuses()
    }
}