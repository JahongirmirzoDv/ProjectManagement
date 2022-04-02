package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingListItem

/**
 *Created by farrukh_kh on 7/24/21 10:04 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.apis
 **/
interface DatingApi {

    @Multipart
    @POST("/api/v1/dating/dating/")
    suspend fun createDating(
//        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("address") address: RequestBody,
        @Part("start_time") startTime: RequestBody,
        @Part("end_time") endTime: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("importance") importance: RequestBody,
//        @Part("reminder") reminder: RequestBody?,
        @Part("new_reminders") reminders: Array<RequestBody>,
//        @Part("members") members: Array<RequestBody>,
        @Part("until_date") untilDate: RequestBody?,
        @Part("repeat") repeat: RequestBody?,
        @Part("partner_ins") partnerIn: Array<RequestBody>?,
        @Part("partner_out") partnerOut: RequestBody?,
        @Part("repeat_rule") repeatRule: RequestBody?,
        @Part("repeat_exceptions") repeatExceptions: @JvmSuppressWildcards List<RequestBody>?
    ): Response<Any>

    @GET("/api/v1/dating/dating-list/")
    suspend fun getDatingList(): Response<List<DatingListItem>>

    @GET("/api/v1/dating/dating-retrieve/{id}")
    suspend fun getDatingById(
        @Path("id") meetingId: Int
    ): Response<DatingDetails>

    @DELETE("/api/v1/dating/dating-delete/{id}/")
    suspend fun deleteDating(
        @Path("id") datingId: Int
    ): Response<Any>

    @Multipart
    @PATCH("/api/v1/dating/dating-update/{id}/")
    suspend fun updateDating(
        @Path("id") datingId: Int,
//        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("partner_ins") partnerIn: Array<RequestBody>?,
        @Part("partner_out") partnerOut: RequestBody?,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("deleted_files") deletedFiles: Array<RequestBody>,
        @Part("importance") importance: RequestBody,
//        @Part("members") participants: Array<RequestBody>?,
//        @Part("reminder") reminder: RequestBody?,
        @Part("until_date") untilDate: RequestBody?,
        @Part("repeat") repeat: RequestBody?,
        @Part("delete_reminders") deletedReminders: Array<RequestBody>,
        @Part("new_reminders") reminders: Array<RequestBody>,
        @Part("repeat_rule") repeatRule: RequestBody?,
        ): Response<Any>
}