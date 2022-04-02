package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingListItem
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * by farrukh_kh
 **/

@HiltViewModel
class MeetingsViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository
) : ViewModel() {

    private val _meetings = MutableStateFlowWrapper<List<MeetingListItem>>()
    val meetings: StateFlowWrapper<List<MeetingListItem>> get() = _meetings.asStateFlow()

    init {
        initMeetings()
    }

    fun initMeetings() = launchCoroutine {
        _meetings.value = DataWrapper.Loading()
        _meetings.value = meetingRepository.getMeetingListNew()
    }
}