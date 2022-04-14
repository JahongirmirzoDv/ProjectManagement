package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.dashboard.select_person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffBelow
import uz.perfectalgorithm.projects.tezkor.domain.home.dashboard.DashboardRepository
import javax.inject.Inject


@HiltViewModel
class AllStaffSelectDashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _staffBelow =
        MutableStateFlow<DataWrapper<List<StaffBelow>>>(DataWrapper.Empty())
    val staffBelow: StateFlow<DataWrapper<List<StaffBelow>>> =
        _staffBelow.asStateFlow()

    init {
        initStaffBelow()
    }

    fun initStaffBelow() = viewModelScope.launch(Dispatchers.IO) {
        _staffBelow.value = DataWrapper.Loading()
        _staffBelow.value = repository.getStaffBelow()
    }
}