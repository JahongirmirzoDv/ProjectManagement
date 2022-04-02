package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.CreateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan.UpdateQuickPlanRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan.QuickPlanRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 19.06.2021 14:54
 **/
@HiltViewModel
class QuickPlansViewModel @Inject constructor(
    private val quickPlanRepository: QuickPlanRepository
) : ViewModel() {

    var mQuickPlans = ArrayList<QuickPlan>()

    private val _quickPlansOfDay =
        MutableStateFlow<DataWrapper<List<QuickPlan>>>(DataWrapper.Empty())
    val quickPlansOfDay: StateFlow<DataWrapper<List<QuickPlan>>> get() = _quickPlansOfDay

    private val _quickPlansOfWeek =
        MutableStateFlow<DataWrapper<List<QuickPlan>>>(DataWrapper.Empty())
    val quickPlansOfWeek: StateFlow<DataWrapper<List<QuickPlan>>> get() = _quickPlansOfWeek

    private val _quickPlansOfMonth =
        MutableStateFlow<DataWrapper<List<QuickPlan>>>(DataWrapper.Empty())
    val quickPlansOfMonth: StateFlow<DataWrapper<List<QuickPlan>>> get() = _quickPlansOfMonth

    private val _quickPlansOfYear =
        MutableStateFlow<DataWrapper<List<QuickPlan>>>(DataWrapper.Empty())
    val quickPlansOfYear: StateFlow<DataWrapper<List<QuickPlan>>> get() = _quickPlansOfYear

    private val _createResponse = MutableStateFlow<DataWrapper<QuickPlan>>(DataWrapper.Empty())
    val createResponse: StateFlow<DataWrapper<QuickPlan>> get() = _createResponse

    private val _updateResponse = MutableStateFlow<DataWrapper<QuickPlan>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<QuickPlan>> get() = _updateResponse


    val allQuickPlans by lazy {
        quickPlanRepository.getAllQuickPlans().cachedIn(viewModelScope)
    }

    fun initQuickPlansOfDay(day: String) = viewModelScope.launch(Dispatchers.IO) {
        _quickPlansOfDay.value = DataWrapper.Loading()
        _quickPlansOfDay.value = quickPlanRepository.getQuickPlansOfDay(day)
    }

    fun initQuickPlansOfWeek(startDay: String) = viewModelScope.launch(Dispatchers.IO) {
        _quickPlansOfWeek.value = DataWrapper.Loading()
        _quickPlansOfWeek.value = quickPlanRepository.getQuickPlansOfWeek(startDay)
    }

    fun initQuickPlansOfMonth(month: String) = viewModelScope.launch(Dispatchers.IO) {
        _quickPlansOfMonth.value = DataWrapper.Loading()
        _quickPlansOfMonth.value = quickPlanRepository.getQuickPlansOfMonth(month)
    }

    fun initQuickPlansOfYear(year: String) = viewModelScope.launch(Dispatchers.IO) {
        _quickPlansOfYear.value = DataWrapper.Loading()
        _quickPlansOfYear.value = quickPlanRepository.getQuickPlansOfYear(year)
    }

    fun createQuickPlan(createQuickPlanRequest: CreateQuickPlanRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _createResponse.value = DataWrapper.Loading()
            _createResponse.value = quickPlanRepository.createQuickPlan(createQuickPlanRequest)
        }

    fun changeStatus(id: Int, isDone: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _updateResponse.value = DataWrapper.Loading()
        _updateResponse.value =
            quickPlanRepository.updateQuickPlan(id, UpdateQuickPlanRequest(isDone = isDone))
    }

    fun changeParent(id: Int, newParentId: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (id != newParentId && id != 0 && newParentId != 0) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value =
                quickPlanRepository.updateQuickPlan(
                    id,
                    UpdateQuickPlanRequest(parentId = newParentId)
                )
        }
    }

    fun changePlanToTop(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (id != 0) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value =
                quickPlanRepository.updateToTopQuickPlan(
                    id
                )
        }
    }

    fun updateQuickPlanPosition(id: Int, upper: Int?, lower: Int?) =
        viewModelScope.launch(Dispatchers.IO) {
            if (id != 0) {
                _updateResponse.value = DataWrapper.Loading()
                _updateResponse.value =
                    quickPlanRepository.updatePositionQuickPlan(
                        id = id,
                        upper = upper,
                        lower = lower
                    )
            }
        }


    fun submitQPList(quickPlans: List<QuickPlan>) {
        mQuickPlans = quickPlans as ArrayList<QuickPlan>
    }
}