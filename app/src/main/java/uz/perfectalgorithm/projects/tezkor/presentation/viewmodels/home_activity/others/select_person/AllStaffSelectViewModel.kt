package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/25/21 10:44 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.others.adding_helpers
 **/
@HiltViewModel
class AllStaffSelectViewModel @Inject constructor(
    private val workersRepository: WorkersRepository
) : ViewModel() {

    private val _workers =
        MutableStateFlow<DataWrapper<List<AllWorkersShort>>>(DataWrapper.Empty())
    val workers: StateFlow<DataWrapper<List<AllWorkersShort>>> get() = _workers

    init {
        initWorkers()
    }

    fun initWorkers() = viewModelScope.launch(Dispatchers.IO) {
        _workers.value = DataWrapper.Loading()
        _workers.value = workersRepository.getAllWorkersShort()

    }
}