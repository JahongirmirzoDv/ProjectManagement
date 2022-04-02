package uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.MeetingApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.MemberStateRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.UpdateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.CheckedMeeting
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingListItem
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/24/21 10:21 AM
 *uz.rdo.projects.projectmanagement.domain.home.task.meeting
 **/
class MeetingRepositoryImpl @Inject constructor(
    private val meetingApi: MeetingApi
) : MeetingRepository {

    override suspend fun createMeeting(request: CreateMeetingRequest) = try {

        val filesBody = ArrayList<MultipartBody.Part>()
        request.files?.forEach { file ->
            filesBody.add(prepareImageFilePart("files", file))
        }

        val membersBody = Array(request.members.size) {
            request.members[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val remindersBody = Array(request.reminders.size) {
            request.reminders[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val discussedTopicsBody = Array(request.discussedTopics.size) {
            request.discussedTopics[it].toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val moderators = Array(request.moderators.size) {
            request.moderators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val response = meetingApi.createMeeting(
            title = request.title.toRequestBody("text/plain".toMediaTypeOrNull()),
            messageID = request.message_id,
            address = request.address.toRequestBody("text/plain".toMediaTypeOrNull()),
            startTime = request.startTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            endTime = request.endTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            files = filesBody,
            repeat = request.repeat?.toRequestBody("text/plain".toMediaTypeOrNull()),
            importance = request.importance.toRequestBody("text/plain".toMediaTypeOrNull()),
//            reminder = request.reminder?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
            reminders = remindersBody,
            untilDate = request.untilDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
            members = membersBody,
            repeatRule = request.repeatRule.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            repeatExceptions = listOf(),
            discussedTopics = discussedTopicsBody,
            canEditTime = request.canEditTime.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            description = request.description?.toRequestBody("text/plain".toMediaTypeOrNull()),
            moderators = moderators
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            timberLog(response.message())
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        timberLog(e.message.toString())
        DataWrapper.Error(e)
    }

    override suspend fun createMeetingComment(request: CreateMeetingComment): DataWrapper<MeetingComment> {
        return try {
            val response = meetingApi.createMeetingComment(request)
            if (response.isSuccessful) {
                DataWrapper.Success(response.body()!!)
            } else {
                timberLog(response.message())
                DataWrapper.Error(HttpException(response))
            }
        } catch (e: Exception) {
            timberLog(e.message.toString())
            DataWrapper.Error(e)
        }
    }

    override fun getMeetingList(): Flow<Result<List<MeetingListItem>>> = flow {
        try {
            val response = meetingApi.getMeetingsList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else emit(
                Result.failure<List<MeetingListItem>>(
                    Exception(
                        App.instance.resources.getString(R.string.error)
                    )
                )
            )
        } catch (e: Exception) {
            timberLog("MeetingRepositoryImpl in function refreshToken error = $e")
            emit(Result.failure<List<MeetingListItem>>(e))
        }
    }

    override suspend fun getMeetingListNew() = try {
        val response = meetingApi.getMeetingsList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getMeetingById(meetingId: Int) = try {
        val response = meetingApi.getMeetingById(meetingId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteMeeting(meetingId: Int) = try {
        val response = meetingApi.deleteMeeting(meetingId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateMeeting(
        meetingId: Int,
        updateMeetingRequest: UpdateMeetingRequest
    ) = try {

        val filesBody = ArrayList<MultipartBody.Part>()
        updateMeetingRequest.newFiles.forEach { file ->
            filesBody.add(prepareImageFilePart("files", file))
        }

        val newMembers = Array(updateMeetingRequest.newMembers.size) {
            updateMeetingRequest.newMembers[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedMembers = Array(updateMeetingRequest.deletedMembers.size) {
            updateMeetingRequest.deletedMembers[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val newDiscussedTopics = Array(updateMeetingRequest.newDiscussedTopics.size) {
            updateMeetingRequest.newDiscussedTopics[it]
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedDiscussedTopics = Array(updateMeetingRequest.deletedDiscussedTopics.size) {
            updateMeetingRequest.deletedDiscussedTopics[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val newReminders = Array(updateMeetingRequest.newReminders.size) {
            updateMeetingRequest.newReminders[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedReminders = Array(updateMeetingRequest.deletedReminders.size) {
            updateMeetingRequest.deletedReminders[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedFilesBody = Array(updateMeetingRequest.deletedFiles.size) {
            updateMeetingRequest.deletedFiles[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val moderators = Array(updateMeetingRequest.moderators.size) {
            updateMeetingRequest.moderators[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val response = meetingApi.updateMeeting(
            meetingId = meetingId,
            title = updateMeetingRequest.title.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = updateMeetingRequest.description?.toRequestBody("text/plain".toMediaTypeOrNull()),
            address = updateMeetingRequest.address.toRequestBody("text/plain".toMediaTypeOrNull()),
            start_time = updateMeetingRequest.startTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            end_time = updateMeetingRequest.endTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            files = filesBody,
            importance = updateMeetingRequest.importance.toRequestBody("text/plain".toMediaTypeOrNull()),
            newMembers = newMembers,
            deletedMembers = deletedMembers,
            repeat = updateMeetingRequest.repeat?.toRequestBody("text/plain".toMediaTypeOrNull()),
            deletedFiles = deletedFilesBody,
//            reminder = updateMeetingRequest.reminder?.toString()
//                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            untilDate = updateMeetingRequest.reminderDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
            meetingStatus = updateMeetingRequest.meetingStatus?.toRequestBody("text/plain".toMediaTypeOrNull()),
            discussedTopics = newDiscussedTopics,
            deletedDiscussedTopics = deletedDiscussedTopics,
            deletedReminders = deletedReminders,
            reminders = newReminders,
            canEditTime = updateMeetingRequest.canEditTime.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            repeatRule = updateMeetingRequest.repeatRule?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            moderators = moderators
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateModerator(meetingId: Int, moderator: List<Int>): DataWrapper<Any> =
        try {
            val request = if (moderator.isNotEmpty())
                Array(moderator.size) {
                    moderator[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                } else null
            val response = meetingApi.updateModerator(meetingId, request)
            if (response.isSuccessful) {
                DataWrapper.Success(response.body() ?: Any())
            } else {
                DataWrapper.Error(HttpException(response))
            }
        } catch (e: Exception) {
            DataWrapper.Error(e)
        }

    override suspend fun updateChecked(id: Int, check: Boolean) =  try {
        val response = meetingApi.checkedMeet(id, check)
        if (response.isSuccessful) {
            DataWrapper.Success<CheckedMeeting>(response.body()?:CheckedMeeting("", -1, false, ""))
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun startEndMeeting(
        meetingId: Int,
        meetingStatus: String
    ) = try {
        val response = meetingApi.startEndMeeting(
            meetingId, meetingStatus.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun changeMemberState(
        memberId: Int,
        memberStateRequest: MemberStateRequest
    ) = try {
        val response = meetingApi.changeMemberState(memberId, memberStateRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}