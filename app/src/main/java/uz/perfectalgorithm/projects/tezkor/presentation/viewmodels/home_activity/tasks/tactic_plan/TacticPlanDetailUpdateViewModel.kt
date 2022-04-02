package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan.UpdateTacticPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlanDetails
import uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan.TacticPlanRepository

/**
 *Created by farrukh_kh on 8/23/21 4:27 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.tactic_plan
 **/
class TacticPlanDetailUpdateViewModel @AssistedInject constructor(
    private val tacticPlanRepository: TacticPlanRepository,
    @Assisted private val tacticPlanId: Int
) : ViewModel() {

    private val _tacticPlan = MutableStateFlow<DataWrapper<TacticPlanDetails>>(DataWrapper.Empty())
    val tacticPlan: StateFlow<DataWrapper<TacticPlanDetails>> get() = _tacticPlan

    private val _updateResponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<Any>> get() = _updateResponse

    private val _statusList =
        MutableStateFlow<DataWrapper<List<Status>>>(DataWrapper.Empty())
    val statusList: StateFlow<DataWrapper<List<Status>>> get() = _statusList

    init {
        initTacticPlan()
        initStatusList()
    }

    fun initTacticPlan() = viewModelScope.launch(Dispatchers.IO) {
        _tacticPlan.value = DataWrapper.Loading()
        _tacticPlan.value = tacticPlanRepository.getTacticPlanById(tacticPlanId)
    }

    fun initStatusList() = viewModelScope.launch(Dispatchers.IO) {
        _statusList.value = DataWrapper.Loading()
        _statusList.value = tacticPlanRepository.getTacticPlanStatuses()
    }

    fun updateQuickPlan(updateTacticPlanRequest: UpdateTacticPlanRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value =
                tacticPlanRepository.updateTacticPlan(tacticPlanId, updateTacticPlanRequest)
        }
}

@AssistedFactory
interface TacticPlanDetailUpdateViewModelFactory {
    fun create(tacticPlanId: Int): TacticPlanDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: TacticPlanDetailUpdateViewModelFactory,
    tacticPlanId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TacticPlanDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(tacticPlanId) as T
        }
        throw IllegalArgumentException("$modelClass is not TacticPlanDetailUpdateViewModel")
    }
}