package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import uz.perfectalgorithm.projects.tezkor.domain.home.task.task_status.TaskStatusRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/26/21 9:36 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.functional
 **/
@HiltViewModel
class CreateStatusViewModel @Inject constructor(
    private val taskStatusRepository: TaskStatusRepository
) : ViewModel() {

    private val _response = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<Any>> get() = _response

    fun createStatus(createStatusRequest: CreateStatusRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskStatusRepository.createStatus(createStatusRequest)
        }

    fun updateStatus(statusId: Int, createStatusRequest: CreateStatusRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskStatusRepository.updateStatus(statusId, createStatusRequest)
        }
}