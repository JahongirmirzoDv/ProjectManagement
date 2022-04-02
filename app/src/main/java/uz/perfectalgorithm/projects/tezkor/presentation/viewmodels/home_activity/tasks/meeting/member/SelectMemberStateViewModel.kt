package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.meeting.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.MemberStateRequest
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/31/21 10:01 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.meeting.member
 **/
@HiltViewModel
class SelectMemberStateViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository
) : ViewModel() {

    private val _response = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<Any>> get() = _response

    fun sendMemberState(memberId: Int, memberStateRequest: MemberStateRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = meetingRepository.changeMemberState(memberId, memberStateRequest)
        }
}