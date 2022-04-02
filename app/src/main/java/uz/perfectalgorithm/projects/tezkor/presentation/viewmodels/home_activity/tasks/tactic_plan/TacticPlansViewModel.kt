package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.tactic_plan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.Status
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.TacticPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.UpdateTacticPlanResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan.TacticPlanRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * by farrukh_kh
 **/
@HiltViewModel
class TacticPlansViewModel @Inject constructor(
    private val tacticPlanRepository: TacticPlanRepository
) : ViewModel() {

    private val _changeStatusResponse = MutableStateFlowWrapper<UpdateTacticPlanResponse>()
    val changeStatusResponse: StateFlowWrapper<UpdateTacticPlanResponse> get() = _changeStatusResponse.asStateFlow()

    private val tacticPlans = MutableStateFlowWrapper<List<TacticPlan>>()
    private val tacticPlanStatuses = MutableStateFlowWrapper<List<Status>>()

    val combinedFlow =
        tacticPlans.combine(tacticPlanStatuses) { wrapperTacticPlan, wrapperStatus ->
            when {
                wrapperTacticPlan is DataWrapper.Loading || wrapperStatus is DataWrapper.Loading -> DataWrapper.Loading()
                wrapperTacticPlan is DataWrapper.Error -> DataWrapper.Error(wrapperTacticPlan.error)
                wrapperStatus is DataWrapper.Error -> DataWrapper.Error(wrapperStatus.error)
                wrapperTacticPlan is DataWrapper.Success && wrapperStatus is DataWrapper.Success -> DataWrapper.Success(
                    Pair(wrapperTacticPlan.data, wrapperStatus.data)
                )
                else -> DataWrapper.Empty()
            }
        }

    fun initTacticPlans() = launchCoroutine {
        tacticPlans.value = DataWrapper.Loading()
        tacticPlans.value = tacticPlanRepository.getTacticPlans()
    }

    fun initTacticPlanStatuses() = launchCoroutine {
        tacticPlanStatuses.value = DataWrapper.Loading()
        tacticPlanStatuses.value = tacticPlanRepository.getTacticPlanStatuses()
    }

    fun changeStatus(tacticPlanId: Int, changeStatusRequest: ChangeStatusRequest) =
        launchCoroutine {
            _changeStatusResponse.value = DataWrapper.Loading()
            _changeStatusResponse.value =
                tacticPlanRepository.changeStatus(tacticPlanId, changeStatusRequest)
        }
}