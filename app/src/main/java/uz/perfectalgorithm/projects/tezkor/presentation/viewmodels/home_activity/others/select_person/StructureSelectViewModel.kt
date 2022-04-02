package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/24/21 11:01 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.others.adding_helpers
 **/
@HiltViewModel
class StructureSelectViewModel @Inject constructor(
    private val workersRepository: WorkersRepository,
) : ViewModel() {

    private val _structure =
        MutableStateFlow<DataWrapper<List<DepartmentStructureShort?>>>(DataWrapper.Empty())
    val structure: StateFlow<DataWrapper<List<DepartmentStructureShort?>>> get() = _structure

    init {
        initStructure()
    }

    fun initStructure() = viewModelScope.launch(Dispatchers.IO) {
        _structure.value = DataWrapper.Loading()
        _structure.value = workersRepository.getStructureShort()
    }
}