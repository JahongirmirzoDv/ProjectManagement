package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal.UpdateTaskWithChatRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.UploadGroupImageFileResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.users.ReceiverDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.UpdateTaskMessageResponse
import java.io.File

/**
 *  *  * Created by Raximjanov Davronbek on 22.09.2021 11:10
 **/

interface ChatAllApi {

    @GET("/api/v1/chat/chat-group-list/")
    suspend fun getGroupChatList(
    ): Response<ChatGroupListResponse.Result>

    @GET("/api/v1/chat/company-chat-group-list/")
    suspend fun getCompanyGroupChatList(
    ): Response<ChatGroupListResponse.Result>

    @GET("/api/v1/chat/chat-project-group-list/")
    suspend fun getProjectChatList(
    ): Response<ChatGroupListResponse.Result>

    @GET("/api/v1/chat/chat-personal-list/")
    suspend fun getPersonalChatList(
    ): Response<PersonalChatListResponse.Result>

    @GET("/api/v1/chat/chat-personal-messages-list/")
    suspend fun getPersonalConversationMessageList(
        @Query("chat_id") chatId: Int,
        @Query("page") page: Int
    ): Response<PersonalChatMessageListResponse.ResponseData>

    @GET("/api/v1/chat/chat-group-messages-list/")
    suspend fun getGroupConversationMessageList(
        @Query("chat_id") chatId: Int,
        @Query("page") page: Int
    ): Response<GroupChatMessageListResponse.ResponseData>

    @Multipart
    @POST("/api/v1/chat/personal-chat/file-upload")
    suspend fun uploadFile(
        @Part("chat_id") chatId: RequestBody,
        @Part("chat_type") chatType: RequestBody,
        @Part part: MultipartBody.Part
    ): Response<ChatFileUploadResponse.Result>

    @Multipart
    @POST("/api/v1/chat/group-chat/file-upload")
    suspend fun uploadFileForGroupChat(
        @Part("chat_id") chatId: RequestBody,
        @Part("chat_type") chatType: RequestBody,
        @Part part: MultipartBody.Part
    ): Response<ChatFileUploadResponse.Result>


    @Multipart
    @PUT("/api/v1/chat/chat-group-update-image/{id}/")
    suspend fun uploadGroupImageFile(
        @Path("id") id: Int,
        @Part part: MultipartBody.Part
    ): Response<UploadGroupImageFileResponse.ResponseData>

    @FormUrlEncoded
    @PATCH("/api/v1/chat/chat-group-title-update/{id}/")
    suspend fun uploadGroup(
        @Path("id") id: Int,
        @Field("title") title: String
    ): Response<ResponseBody>

    @Multipart
    @PATCH("/api/v1/chat/chat-group-title-update/{id}/")
    suspend fun uploadGroupWithImg(
        @Path("id") id: Int,
        @Part("title") title: String,
        @Part part: MultipartBody.Part? = null,
    ): Response<ResponseBody>

    @PATCH("/api/v1/task/task-update/{id}/")
    suspend fun updateTaskMessageIsDone(
        @Path("id") id: Int,
        @Body updateTaskWithChatRequest: UpdateTaskWithChatRequest
    ): Response<UpdateTaskMessageResponse.Result>

    @GET("/api/v1/user/staff-retrieve/{id}/")
    suspend fun getReceiverUserData(
        @Path("id") id: Int
    ): Response<ReceiverDataResponse.Result>

    @GET("/api/v1/company/short-all-staffs/")
    suspend fun getAllWorkers(
    ): Response<AllWorkersShortDataResponse.Result>

    @GET("/api/v1/company/short-user-favourites/")
    suspend fun getTeamWorkers(
    ): Response<AllWorkersShortDataResponse.Result>

    @GET("/api/v1/company/short-structure/")
    suspend fun getStructureWorkers(
    ): Response<AllWorkersShortDataResponse.Result>

    @GET("/api/v1/chat/chat-group-retrieve/{id}/")
    suspend fun getGroupChatDetail(
        @Path("id") chatId: Int
    ): Response<GroupChatDetailResponse.ResponseData>


}