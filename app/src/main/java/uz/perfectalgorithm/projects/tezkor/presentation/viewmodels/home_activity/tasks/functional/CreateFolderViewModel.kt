package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.folder.CreateFolderRequest
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/24/21 3:51 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.functional
 **/
@HiltViewModel
class CreateFolderViewModel @Inject constructor(
    private val taskRepository: FunctionalTaskRepository
) : ViewModel() {

    private val _response = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<Any>> get() = _response


    fun createFolder(createFolderRequest: CreateFolderRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskRepository.createTaskFolder(createFolderRequest)
        }

    fun updateFolder(folderId: Int, createFolderRequest: CreateFolderRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskRepository.updateTaskFolder(folderId, createFolderRequest)
        }
}