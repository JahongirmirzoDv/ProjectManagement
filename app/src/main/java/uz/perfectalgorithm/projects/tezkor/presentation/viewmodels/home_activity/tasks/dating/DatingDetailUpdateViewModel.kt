package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.dating

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.UpdateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.dating.DatingRepository
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateViewModel

/**
 *Created by farrukh_kh on 8/11/21 10:54 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.dating
 **/
class DatingDetailUpdateViewModel @AssistedInject constructor(
    private val datingRepository: DatingRepository,
    private val repetitionRepository: RepetitionRepository,
    @Assisted private val datingId: Int
) : ViewModel(), DetailUpdateViewModel {

    private val _dating = MutableStateFlow<DataWrapper<DatingDetails>>(DataWrapper.Empty())
    val dating: StateFlow<DataWrapper<DatingDetails>> get() = _dating

    private val _repeats = MutableStateFlow<DataWrapper<List<RepetitionData>>>(DataWrapper.Empty())
    val repeats: StateFlow<DataWrapper<List<RepetitionData>>> get() = _repeats

    private val _updateResponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<Any>> get() = _updateResponse

    init {
        initDating()
        initRepeats()
    }

    fun initDating() = viewModelScope.launch(Dispatchers.IO) {
        _dating.value = DataWrapper.Loading()
        _dating.value = datingRepository.getDatingById(datingId)
    }

    fun initRepeats() = viewModelScope.launch(Dispatchers.IO) {
        _repeats.value = DataWrapper.Loading()
        _repeats.value = repetitionRepository.getRepetitionsNew()
    }

    fun updateDating(updateDatingRequest: UpdateDatingRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value = datingRepository.updateDating(datingId, updateDatingRequest)
        }

    fun clearUpdateResponse() {
        _updateResponse.value = DataWrapper.Empty()
    }

//    override fun initStatusList() = viewModelScope.launch {}
}

@AssistedFactory
interface DatingDetailUpdateViewModelFactory {
    fun create(datingId: Int): DatingDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: DatingDetailUpdateViewModelFactory,
    datingId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatingDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(datingId) as T
        }
        throw IllegalArgumentException("$modelClass is not DatingDetailUpdateViewModel")
    }
}