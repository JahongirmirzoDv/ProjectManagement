package uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.MemberStateRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.UpdateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.CheckedMeeting
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingListItem

/**
 *Created by farrukh_kh on 7/24/21 10:21 AM
 *uz.rdo.projects.projectmanagement.domain.home.task.meeting
 **/
interface MeetingRepository {

    suspend fun createMeeting(request: CreateMeetingRequest): DataWrapper<Any>
    suspend fun createMeetingComment(request: CreateMeetingComment): DataWrapper<MeetingComment>

    fun getMeetingList(): Flow<Result<List<MeetingListItem>>>

    suspend fun getMeetingListNew(): DataWrapper<List<MeetingListItem>>

    suspend fun getMeetingById(meetingId: Int): DataWrapper<MeetingDetails>

    suspend fun deleteMeeting(meetingId: Int): DataWrapper<Any>

    suspend fun updateMeeting(
        meetingId: Int,
        updateMeetingRequest: UpdateMeetingRequest
    ): DataWrapper<Any>

    suspend fun updateModerator(
        meetingId: Int,
        moderator:List<Int>
    ): DataWrapper<Any>

    suspend fun updateChecked(id: Int, check: Boolean): DataWrapper<CheckedMeeting>

    suspend fun startEndMeeting(
        meetingId: Int,
        meetingStatus: String
    ): DataWrapper<Any>

    suspend fun changeMemberState(
        memberId: Int,
        memberStateRequest: MemberStateRequest
    ): DataWrapper<Any>
}