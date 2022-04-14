package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepository
import javax.inject.Inject


@HiltViewModel
class CreateTaskDialogViewModel @Inject constructor(
    private val taskRepository: FunctionalTaskRepository
) : ViewModel() {

    private val _response = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<Any>> get() = _response

    fun createTask(folderId: Int, statusId: Int, title: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskRepository.createSimpleTask(folderId, statusId, title)
        }
}