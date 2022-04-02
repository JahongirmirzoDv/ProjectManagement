package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting

import androidx.lifecycle.MutableLiveData
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.UpdateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.CheckedMeeting
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepository
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateViewModel
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine

/**
 *Created by farrukh_kh on 8/11/21 10:54 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.meeting
 **/
class MeetingDetailUpdateViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val repetitionRepository: RepetitionRepository,
    @Assisted private val meetingId: Int
) : ViewModel(), DetailUpdateViewModel {

    private val _meeting = MutableStateFlow<DataWrapper<MeetingDetails>>(DataWrapper.Empty())
    val meeting: StateFlow<DataWrapper<MeetingDetails>> get() = _meeting

    private val _repeats = MutableStateFlow<DataWrapper<List<RepetitionData>>>(DataWrapper.Empty())
    val repeats: StateFlow<DataWrapper<List<RepetitionData>>> get() = _repeats

    private val _updateResponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<Any>> get() = _updateResponse

    private val _updateResponseChecked = MutableStateFlow<DataWrapper<CheckedMeeting>>(DataWrapper.Empty())
    val updateResponseChecked: StateFlow<DataWrapper<CheckedMeeting>> get() = _updateResponseChecked

    private val _startEndReponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val startEndReponse: StateFlow<DataWrapper<Any>> get() = _startEndReponse
    private val _createComment = MutableStateFlow<DataWrapper<MeetingComment>>(DataWrapper.Empty())
    val createComment: StateFlow<DataWrapper<MeetingComment>> get() = _createComment

    val commentsList: MutableLiveData<List<MeetingComment>> = MutableLiveData()
    val comments: ArrayList<MeetingComment> = arrayListOf()
    var parentCommentId: Int? = null

    init {
        initMeeting()
    }

    fun initMeeting() = viewModelScope.launch(Dispatchers.IO) {
        _meeting.value = DataWrapper.Loading()
        _meeting.value = meetingRepository.getMeetingById(meetingId)
    }

    fun initRepeats() = viewModelScope.launch(Dispatchers.IO) {
        _repeats.value = DataWrapper.Loading()
        _repeats.value = repetitionRepository.getRepetitionsNew()
    }

    fun updateMeeting(updateMeetingRequest: UpdateMeetingRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value = meetingRepository.updateMeeting(meetingId, updateMeetingRequest)
        }

    fun createMeetingComment(text: String) = launchCoroutine {
        val result = meetingRepository.createMeetingComment(
            CreateMeetingComment(
                meetingId,
                text,
                parentCommentId
            )
        )
        _createComment.value = result
        if (result is DataWrapper.Success) {
            comments.add(result.data)
            commentsList.postValue(comments)
        }
    }

    fun updateModeratorMeeting(moderator: List<Int>) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value = meetingRepository.updateModerator(meetingId, moderator)
        }

    fun updateCheckedMeeting(id: Int, checked: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponseChecked.value = DataWrapper.Loading()
            _updateResponseChecked.value = meetingRepository.updateChecked(id, checked)
        }

    fun startEndMeeting(meetingStatus: String) = viewModelScope.launch(Dispatchers.IO) {
        _startEndReponse.value = DataWrapper.Loading()
        _startEndReponse.value = meetingRepository.startEndMeeting(meetingId, meetingStatus)
    }

    fun clearUpdateResponse() {
        _updateResponse.value = DataWrapper.Empty()
    }

//    override fun initStatusList() = viewModelScope.launch {}
}

@AssistedFactory
interface MeetingDetailUpdateViewModelFactory {
    fun create(meetingId: Int): MeetingDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: MeetingDetailUpdateViewModelFactory,
    meetingId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(meetingId) as T
        }
        throw IllegalArgumentException("$modelClass is not MeetingDetailUpdateViewModel")
    }
}