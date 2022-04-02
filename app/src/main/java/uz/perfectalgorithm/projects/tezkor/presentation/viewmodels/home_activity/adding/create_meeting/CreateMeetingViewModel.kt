package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_meeting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 19.06.2021 17:48
 **/

@HiltViewModel
class CreateMeetingViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
//    private val repetitionRepository: RepetitionRepository
) : ViewModel() {

    private val _createResponse = MutableStateFlowWrapper<Any>()
    val createResponse: StateFlowWrapper<Any> get() = _createResponse.asStateFlow()

    private val _createComment = MutableStateFlowWrapper<MeetingComment>()
    val createResponseComment: StateFlowWrapper<MeetingComment> get() = _createComment.asStateFlow()

//    private val _repetitions = MutableStateFlowWrapper<List<RepetitionData>>()
//    val repetitions: StateFlowWrapper<List<RepetitionData>> get() = _repetitions.asStateFlow()

//    init {
//        initRepetitions()
//    }

    fun createMeeting(request: CreateMeetingRequest) = launchCoroutine {
        _createResponse.value = DataWrapper.Loading()
        _createResponse.value = meetingRepository.createMeeting(request)
    }

    fun createMeetingComment(text: String, meetingId: Int) = launchCoroutine {
        _createComment.value =
            meetingRepository.createMeetingComment(CreateMeetingComment(meetingId, text))

    }

//    fun initRepetitions() = launchCoroutine {
//        _repetitions.value = DataWrapper.Loading()
//        _repetitions.value = repetitionRepository.getRepetitionsNew()
//    }
}