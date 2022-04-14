package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_dating

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.CreateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.dating.DatingRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject


@HiltViewModel
class CreateDatingViewModel @Inject constructor(
    private val datingRepository: DatingRepository,
//    private val repetitionRepository: RepetitionRepository,
) : ViewModel() {

    private val _createResponse = MutableStateFlowWrapper<Any>()
    val createResponse: StateFlowWrapper<Any> get() = _createResponse.asStateFlow()

//    private val _repetitions = MutableStateFlowWrapper<List<RepetitionData>>()
//    val repetitions: StateFlowWrapper<List<RepetitionData>> get() = _repetitions.asStateFlow()

//    init {
//        initRepetitions()
//    }

    fun createDating(request: CreateDatingRequest) = launchCoroutine {
        _createResponse.value = DataWrapper.Loading()
        _createResponse.value = datingRepository.createDating(request)
    }

//    fun initRepetitions() = launchCoroutine {
//        _repetitions.value = DataWrapper.Loading()
//        _repetitions.value = repetitionRepository.getRepetitionsNew()
//    }
}