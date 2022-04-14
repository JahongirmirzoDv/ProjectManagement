package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.goal.UpdateGoalRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateViewModel


class GoalDetailUpdateViewModel @AssistedInject constructor(
    private val goalMapRepository: GoalMapRepository,
    @Assisted private val goalId: Int
) : ViewModel(), DetailUpdateViewModel {

    private val _goal = MutableStateFlow<DataWrapper<GoalDetails>>(DataWrapper.Empty())
    val goal: StateFlow<DataWrapper<GoalDetails>> get() = _goal

    private val _folders =
        MutableStateFlow<DataWrapper<List<ItemGoalMapData>>>(DataWrapper.Empty())
    val folders: StateFlow<DataWrapper<List<ItemGoalMapData>>> get() = _folders

    private val _updateResponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<Any>> get() = _updateResponse

    init {
        initGoal()
        initFolders()
    }

    fun initGoal() = viewModelScope.launch(Dispatchers.IO) {
        _goal.value = DataWrapper.Loading()
        _goal.value = goalMapRepository.getGoalById(goalId)
    }

    fun initFolders() = viewModelScope.launch(Dispatchers.IO) {
        _folders.value = DataWrapper.Loading()
        _folders.value = goalMapRepository.getGoalMapsNew()
    }

    fun updateGoal(updateGoalRequest: UpdateGoalRequest) = viewModelScope.launch(Dispatchers.IO) {
        _updateResponse.value = DataWrapper.Loading()
        _updateResponse.value = goalMapRepository.updateGoal(goalId, updateGoalRequest)
    }

    fun clearUpdateResponse() {
        _updateResponse.value = DataWrapper.Empty()
    }

//    override fun initStatusList() = viewModelScope.launch { }
}

@AssistedFactory
interface GoalDetailUpdateViewModelFactory {
    fun create(goalId: Int): GoalDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: GoalDetailUpdateViewModelFactory,
    goalId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(goalId) as T
        }
        throw IllegalArgumentException("$modelClass is not GoalDetailUpdateViewModel")
    }
}