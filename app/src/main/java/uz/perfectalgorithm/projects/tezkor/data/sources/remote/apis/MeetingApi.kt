package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.MemberStateRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.CheckedMeeting
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingComment
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingListItem

/**
 *Created by farrukh_kh on 7/24/21 10:03 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.apis
 **/
interface MeetingApi {

    @Multipart
    @POST("/api/v1/meeting/meeting/")
    suspend fun createMeeting(
        @Part("title") title: RequestBody,
        @Part("message_id") messageID: Int?,
        @Part("address") address: RequestBody,
        @Part("start_time") startTime: RequestBody,
        @Part("end_time") endTime: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("importance") importance: RequestBody,
//        @Part("reminder") reminder: RequestBody?,
        @Part("new_reminders") reminders: Array<RequestBody>?,
        @Part("members") members: Array<RequestBody>,
        @Part("until_date") untilDate: RequestBody?,
        @Part("repeat") repeat: RequestBody?,
        @Part("repeat_rule") repeatRule: RequestBody?,
        @Part("repeat_exceptions") repeatExceptions: @JvmSuppressWildcards List<RequestBody>?,
        @Part("discussed_topics") discussedTopics: Array<RequestBody>,
        @Part("is_editable") canEditTime: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("moderators") moderators: Array<RequestBody>
    ): Response<Any>

    @POST("/api/v1/meeting/meeting-comment/")
    suspend fun createMeetingComment(
        @Body request: CreateMeetingComment
    ): Response<MeetingComment>

    @GET("/api/v1/meeting/meeting-list/")
    suspend fun getMeetingsList(): Response<List<MeetingListItem>>

    @GET("/api/v1/meeting/meeting-retrieve/{id}")
    suspend fun getMeetingById(
        @Path("id") meetingId: Int
    ): Response<MeetingDetails>

    @DELETE("/api/v1/meeting/meeting-delete/{id}/")
    suspend fun deleteMeeting(
        @Path("id") meetingId: Int
    ): Response<Any>

    @Multipart
    @PATCH("/api/v1/meeting/meeting-update/{id}/")
    suspend fun updateMeeting(
        @Path("id") meetingId: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("deleted_files") deletedFiles: Array<RequestBody>,
        @Part("importance") importance: RequestBody,
        @Part("members") newMembers: Array<RequestBody>,
        @Part("deleted_members") deletedMembers: Array<RequestBody>,
//        @Part("reminder") reminder: RequestBody?,
        @Part("until_date") untilDate: RequestBody?,
        @Part("repeat") repeat: RequestBody?,
        @Part("status") meetingStatus: RequestBody?,
        @Part("remove_discussed_topics") deletedDiscussedTopics: Array<RequestBody>,
        @Part("discussed_topics") discussedTopics: Array<RequestBody>,
        @Part("delete_reminders") deletedReminders: Array<RequestBody>,
        @Part("new_reminders") reminders: Array<RequestBody>,
        @Part("is_editable") canEditTime: RequestBody?,
        @Part("repeat_rule") repeatRule: RequestBody?,
        @Part("moderators") moderators: Array<RequestBody>
    ): Response<Any>

    @Multipart
    @PATCH("/api/v1/meeting/meeting-update/{id}/")
    suspend fun updateModerator(
        @Path("id") meetingId: Int,
        @Part("moderators") moderators: Array<RequestBody>?
    ): Response<Any>

    /**
     * majlisni boshlash/tugatish uchun api
     */
    @Multipart
    @PATCH("/api/v1/meeting/meeting-update/{id}/")
    suspend fun startEndMeeting(
        @Path("id") meetingId: Int,
        @Part("status") meetingRequest: RequestBody,
    ): Response<Any>

    @PATCH("/api/v1/meeting/meeting-member-update/{id}/")
    suspend fun changeMemberState(
        @Path("id") memberId: Int,
        @Body memberStateRequest: MemberStateRequest
    ): Response<Any>

    @FormUrlEncoded
    @PATCH("api/v1/meeting/meeting-member-update/{id}/")
    suspend fun checkedMeet(
        @Path("id") memberId: Int,
        @Field("participated") participated: Boolean
    ): Response<CheckedMeeting>
}