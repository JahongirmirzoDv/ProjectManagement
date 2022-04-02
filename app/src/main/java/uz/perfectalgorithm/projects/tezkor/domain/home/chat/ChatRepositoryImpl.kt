package uz.perfectalgorithm.projects.tezkor.domain.home.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.ChatTypeEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ChatAllApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal.UpdateTaskWithChatRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.UpdateTaskMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import java.io.File
import javax.inject.Inject

/**
 *  * Created by Raximjanov Davronbek on 22.09.2021 11:10
 **/

/**
 * Ushbu repo: Chat bo'limi uchun faqatgina <http> orqali so'rovlar uchun yaratilgan
 *
 * **/

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatAllApi,
    private val storage: LocalStorage,
) : ChatRepository {

    override var emptyMessagesListener: ((Boolean) -> Unit)? = null

    override fun emptyListener(f: SingleBlock<Boolean>) {
        emptyMessagesListener = f
    }

    override var userId: Int = 0
    override var userFullName: String = ""

    // this todo 10.05.2021 13:27
    override var receiverUserShortData: PersonalChatMessageListResponse.Creator =
        PersonalChatMessageListResponse.Creator()

    override var currentGroupChatData: ChatGroupListResponse.GroupChatDataItem =
        ChatGroupListResponse.GroupChatDataItem()

    override fun getPersonalChatList(): Flow<Result<List<PersonalChatListResponse.PersonalChatDataItem>>> =
        flow {
            try {
                val response = api.getPersonalChatList()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<PersonalChatListResponse.PersonalChatDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Group Chat error = $e")
                emit(Result.failure<List<PersonalChatListResponse.PersonalChatDataItem>>(e))
            }
        }

    override fun getGroupChatList(): Flow<Result<List<ChatGroupListResponse.GroupChatDataItem>>> =
        flow {
            try {
                val response = api.getGroupChatList()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<ChatGroupListResponse.GroupChatDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Group Chat error = $e")
                emit(Result.failure<List<ChatGroupListResponse.GroupChatDataItem>>(e))
            }
        }

    override fun getCompanyGroupChatList(): Flow<Result<List<ChatGroupListResponse.GroupChatDataItem>>> =
        flow {
            try {
                val response = api.getCompanyGroupChatList()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<ChatGroupListResponse.GroupChatDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Group Chat error = $e")
                emit(Result.failure<List<ChatGroupListResponse.GroupChatDataItem>>(e))
            }
        }

    override fun getProjectChatList(): Flow<Result<List<ChatGroupListResponse.GroupChatDataItem>>> =
        flow {
            try {
                val response = api.getProjectChatList()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<ChatGroupListResponse.GroupChatDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Group Chat error = $e")
                emit(Result.failure<List<ChatGroupListResponse.GroupChatDataItem>>(e))
            }
        }


    override fun getPersonalConversationMessageList(page: Int): Flow<Result<List<PersonalChatMessageListResponse.MessageDataItem>>> =
        flow {
            try {
                val response =
                    api.getPersonalConversationMessageList(chatId = storage.chatId, page = page)
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.results))
                } else emit(
                    Result.failure<List<PersonalChatMessageListResponse.MessageDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Personal Chat Messages error = $e")
                emit(Result.failure<List<PersonalChatMessageListResponse.MessageDataItem>>(e))
            }
        }

    override fun getGroupConversationMessageList(page: Int): Flow<Result<List<GroupChatSendMessageResponse.MessageDataItem>>> =
        flow {
            try {
                val response =
                    api.getGroupConversationMessageList(chatId = storage.chatId, page = page)
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.results))
                } else emit(
                    Result.failure<List<GroupChatSendMessageResponse.MessageDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Personal Chat Messages error = $e")
                emit(Result.failure<List<GroupChatSendMessageResponse.MessageDataItem>>(e))
            }
        }

    override fun uploadFile(file: File): Flow<Result<ChatFileUploadResponse.FileUploadData>> =
        flow {

            try {
                val filePart = MultipartBody.Part.createFormData(
                    "file", "image.jpg", RequestBody.create(
                        "image/JPEG".toMediaTypeOrNull(),
                        file.readBytes()
                    )
                )

                val responseA = api.uploadFile(
                    part = filePart,
                    chatId = storage.chatId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    chatType = ChatTypeEnum.PERSONAL.text.toRequestBody("text/plain".toMediaTypeOrNull()),
                )

                if (responseA.code() == 201) {
                    responseA.body().let {
                        emit(Result.success(it!!.data) as Result<ChatFileUploadResponse.FileUploadData>)
                    }
                } else {
                    Result.failure<List<ChatFileUploadResponse.FileUploadData>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Worker error = $e")
                emit(Result.failure<ChatFileUploadResponse.FileUploadData>(e))
            }

        }

    override fun uploadFileForGroupChat(file: File): Flow<Result<ChatFileUploadResponse.FileUploadData>> =
        flow {

            try {
                val filePart = MultipartBody.Part.createFormData(
                    "file", "image.jpg", RequestBody.create(
                        "image/JPEG".toMediaTypeOrNull(),
                        file.readBytes()
                    )
                )

                val responseA = api.uploadFileForGroupChat(
                    part = filePart,
                    chatId = storage.chatId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    chatType = ChatTypeEnum.PERSONAL.text.toRequestBody("text/plain".toMediaTypeOrNull()),
                )

                if (responseA.code() == 201) {
                    responseA.body().let {
                        emit(Result.success(it!!.data) as Result<ChatFileUploadResponse.FileUploadData>)
                    }
                } else {
                    Result.failure<List<ChatFileUploadResponse.FileUploadData>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Worker error = $e")
                emit(Result.failure<ChatFileUploadResponse.FileUploadData>(e))
            }

        }

    override fun uploadGroupImageFile(
        file: File,
        id: Int
    ): Flow<Result<String>> =
        flow {

            try {
                val filePart = MultipartBody.Part.createFormData(
                    "image", "image.jpg", RequestBody.create(
                        "image/JPEG".toMediaTypeOrNull(),
                        file.readBytes()
                    )
                )

                val responseA = api.uploadGroupImageFile(
                    part = filePart,
                    id = id
                )

                if (responseA.code() == 200) {
                    responseA.body().let {
                        emit(Result.success(it!!.data?.image.toString()))
                    }
                } else {
                    Result.failure<List<String>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Worker error = $e")
                emit(Result.failure<String>(e))
            }
        }


    override fun updateTaskMessage(
        id: Int,
        isDone: Boolean
    ): Flow<Result<UpdateTaskMessageResponse.Data>> =
        flow {
            try {
                val response = api.updateTaskMessageIsDone(
                    id = id,
                    UpdateTaskWithChatRequest(isDone = isDone)
                )
                if (response.code() == 200) {
                    response.body().let {
                        emit(Result.success(it!!.data) as Result<UpdateTaskMessageResponse.Data>)
                    }
                } else {
                    Result.failure<List<UpdateTaskMessageResponse.Data>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Worker error = $e")
                emit(Result.failure<UpdateTaskMessageResponse.Data>(e))
            }
        }

    override fun getReceiverData(
    ): Flow<Result<AllWorkersResponse.DataItem>> =
        flow {
            try {
                val response = api.getReceiverUserData(
                    id = receiverUserShortData.id!!
                )
                if (response.code() == 200) {
                    response.body().let {
                        emit(Result.success(it!!.data) as Result<AllWorkersResponse.DataItem>)
                    }
                } else {
                    Result.failure<List<AllWorkersResponse.DataItem>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Worker error = $e")
                emit(Result.failure<AllWorkersResponse.DataItem>(e))
            }
        }

    override fun getTeamWorkers(): Flow<Result<List<AllWorkersShortDataResponse.WorkerShortDataItem>>> =
        flow {
            try {
                val response = api.getTeamWorkers()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<AllWorkersShortDataResponse.WorkerShortDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Team Workers error = $e")
                emit(Result.failure<List<AllWorkersShortDataResponse.WorkerShortDataItem>>(e))
            }
        }

    override fun getAllWorkers(): Flow<Result<List<AllWorkersShortDataResponse.WorkerShortDataItem>>> =
        flow {
            try {
                val response = api.getAllWorkers()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<AllWorkersShortDataResponse.WorkerShortDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Team Workers error = $e")
                emit(Result.failure<List<AllWorkersShortDataResponse.WorkerShortDataItem>>(e))
            }
        }

    override fun getGroupDetailData(): Flow<Result<GroupChatDetailResponse.ResponseData>> =
        flow {
            try {
                val response = api.getGroupChatDetail(
                    chatId = storage.chatId
                )
                if (response.code() == 200) {
                    response.body().let {
                        emit(Result.success(it!!) as Result<GroupChatDetailResponse.ResponseData>)
                    }
                } else {
                    Result.failure<List<GroupChatDetailResponse.ResponseData>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Get Chat Detail error = $e")
                emit(Result.failure<GroupChatDetailResponse.ResponseData>(e))
            }
        }

    override fun getGroupDetailUpdateData(title: String): Flow<Result<ResponseBody>> =
        flow {
            try {
                val response = api.uploadGroup(
                    storage.chatId,
                    title
                )
                if (response.code() == 200) {
                    response.body().let {
                        emit(Result.success(it!!))
                    }
                } else {
                    Result.failure<List<GroupChatDetailResponse.ResponseData>>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Get Chat Detail error = $e")
                emit(Result.failure<ResponseBody>(e))
            }
        }

    override fun getGroupDetailUpdateDataWithIMg(
        title: String,
        img: File
    ): Flow<Result<ResponseBody>> =
        flow {
            try {
                val imagePart = MultipartBody.Part.createFormData(
                    "image", "companyLogo.jpg", RequestBody.create(
                        "image/JPEG".toMediaTypeOrNull(),
                        img.readBytes()
                    )
                )
                val response = api.uploadGroupWithImg(
                    storage.chatId,
                    title, imagePart
                )
                if (response.code() == 200) {
                    response.body().let {
                        emit(Result.success(it!!))
                    }
                } else {
                    Result.failure<ResponseBody>(
                        Exception(
                            App.instance.resources.getString(R.string.error)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Get Chat Detail error = $e")
                emit(Result.failure<ResponseBody>(e))
            }
        }

}