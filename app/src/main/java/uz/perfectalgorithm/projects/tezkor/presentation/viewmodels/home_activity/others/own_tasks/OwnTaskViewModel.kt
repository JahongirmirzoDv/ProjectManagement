package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.own_tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.OwnTaskItem
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_task.CreateTaskRepository
import javax.inject.Inject


@HiltViewModel
class OwnTaskViewModel @Inject constructor(
    private val taskRepository: CreateTaskRepository

) : ViewModel() {

    private val _ownTask =
        MutableStateFlow<DataWrapper<List<OwnTaskItem>>>(DataWrapper.Empty())
    val ownTask: StateFlow<DataWrapper<List<OwnTaskItem>>> get() = _ownTask

    init {
        initMeetings()
    }

    fun initMeetings() = viewModelScope.launch(Dispatchers.IO) {
        _ownTask.value = DataWrapper.Loading()
        _ownTask.value = taskRepository.getOwnTaskList()
    }

}