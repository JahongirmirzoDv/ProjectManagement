package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.select_person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject


@HiltViewModel
class OutsourceSelectViewModel @Inject constructor(
    private val workersRepository: WorkersRepository
) : ViewModel() {

    private val _workers =
        MutableStateFlow<DataWrapper<List<AllWorkersResponse.DataItem>>>(DataWrapper.Empty())
    val workers: StateFlow<DataWrapper<List<AllWorkersResponse.DataItem>>> get() = _workers

    init {
        initWorkers()
    }

    fun initWorkers() = viewModelScope.launch(Dispatchers.IO) {
        _workers.value = DataWrapper.Loading()
        _workers.value = workersRepository.getOutsourceWorkersNew()
    }
}