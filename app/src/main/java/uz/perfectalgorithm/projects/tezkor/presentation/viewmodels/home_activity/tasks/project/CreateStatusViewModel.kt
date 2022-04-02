package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status.ProjectStatusRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/5/21 10:39 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.project
 **/
@HiltViewModel
class CreateStatusViewModel @Inject constructor(
    private val projectStatusRepository: ProjectStatusRepository
) : ViewModel() {

    private val _response = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<Any>> get() = _response

    fun createStatus(createStatusRequest: CreateStatusRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = projectStatusRepository.createStatus(createStatusRequest)
        }

    fun updateStatus(statusId: Int, createStatusRequest: CreateStatusRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = projectStatusRepository.updateStatus(statusId, createStatusRequest)
        }
}