package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_plans

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan.QuickPlanRepository

/**
 *Created by farrukh_kh on 8/19/21 9:10 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.quick_plans
 **/
class QuickPlanDetailUpdateViewModel @AssistedInject constructor(
    private val quickPlanRepository: QuickPlanRepository,
    @Assisted private val quickPlanId: Int
) : ViewModel() {

    private val _quickPlan = MutableStateFlow<DataWrapper<QuickPlan>>(DataWrapper.Empty())
    val quickPlan: StateFlow<DataWrapper<QuickPlan>> get() = _quickPlan

    init {
        initQuickPlan()
    }

    fun initQuickPlan() = viewModelScope.launch(Dispatchers.IO) {
        _quickPlan.value = DataWrapper.Loading()
        _quickPlan.value = quickPlanRepository.getQuickPlanById(quickPlanId)
    }

    fun updateQuickPlan(updateQuickPlanRequest: UpdateQuickPlanRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _quickPlan.value = DataWrapper.Loading()
            _quickPlan.value =
                quickPlanRepository.updateQuickPlan(quickPlanId, updateQuickPlanRequest)
        }
}

@AssistedFactory
interface QuickPlanDetailUpdateViewModelFactory {
    fun create(quickPlanId: Int): QuickPlanDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: QuickPlanDetailUpdateViewModelFactory,
    quickPlanId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuickPlanDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(quickPlanId) as T
        }
        throw IllegalArgumentException("$modelClass is not QuickPlanDetailUpdateViewModel")
    }
}