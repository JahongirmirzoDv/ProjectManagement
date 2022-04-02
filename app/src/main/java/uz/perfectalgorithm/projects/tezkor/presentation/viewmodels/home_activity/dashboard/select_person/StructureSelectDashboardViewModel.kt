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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.domain.home.dashboard.DashboardRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/24/21 11:01 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.others.adding_helpers
 **/
@HiltViewModel
class StructureSelectDashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _structureBelow =
        MutableStateFlow<DataWrapper<List<DepartmentStructureBelow>>>(DataWrapper.Empty())
    val structureBelow: StateFlow<DataWrapper<List<DepartmentStructureBelow>>> =
        _structureBelow.asStateFlow()

    init {
        initStructureBelow()
    }

    fun initStructureBelow() = viewModelScope.launch(Dispatchers.IO) {
        _structureBelow.value = DataWrapper.Loading()
        _structureBelow.value = repository.getStructureBelow()
    }
}