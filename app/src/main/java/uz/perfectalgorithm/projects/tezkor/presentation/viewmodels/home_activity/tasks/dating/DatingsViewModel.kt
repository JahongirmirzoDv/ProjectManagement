package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.dating

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingListItem
import uz.perfectalgorithm.projects.tezkor.domain.home.task.dating.DatingRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * by farrukh_kh
 **/
@HiltViewModel
class DatingsViewModel @Inject constructor(
    private val datingRepository: DatingRepository
) : ViewModel() {

    private val _datings = MutableStateFlowWrapper<List<DatingListItem>>()
    val datings: StateFlowWrapper<List<DatingListItem>> get() = _datings.asStateFlow()

    init {
        initDating()
    }

    fun initDating() = launchCoroutine {
        _datings.value = DataWrapper.Loading()
        _datings.value = datingRepository.getDatingListNew()
    }
}