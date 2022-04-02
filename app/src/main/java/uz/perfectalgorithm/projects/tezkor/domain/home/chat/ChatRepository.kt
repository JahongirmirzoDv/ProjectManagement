package uz.perfectalgorithm.projects.tezkor.domain.home.chat

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.UploadGroupImageFileResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.UpdateTaskMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import java.io.File

/**
 * Created by Raximjanov Davronbek on 22.09.2021 11:10
 **/

interface ChatRepository {

    val emptyMessagesListener: ((Boolean) -> Unit)?

    fun emptyListener(f: SingleBlock<Boolean>)

    var userId: Int
    var userFullName: String
    var receiverUserShortData: PersonalChatMessageListResponse.Creator
    var currentGroupChatData: ChatGroupListResponse.GroupChatDataItem

    fun getPersonalChatList(): Flow<Result<List<PersonalChatListResponse.PersonalChatDataItem>>>
    fun getGroupChatList(): Flow<Result<List<ChatGroupListResponse.GroupChatDataItem>>>

    fun getCompanyGroupChatList(): Flow<Result<List<ChatGroupListResponse.GroupChatDataItem>>>

    fun getProjectChatList(): Flow<Result<List<ChatGroupListResponse.GroupChatDataItem>>>


//        fun getPersonalConversationMessageList(scope: CoroutineScope): Flow<PagingData<PersonalChatMessageListResponse.MessageDataItem>>

    //  Http Rest Api r/r
    fun getPersonalConversationMessageList(page: Int): Flow<Result<List<PersonalChatMessageListResponse.MessageDataItem>>>

    fun uploadFile(
        file: File
    ): Flow<Result<ChatFileUploadResponse.FileUploadData>>

    fun uploadFileForGroupChat(
        file: File
    ): Flow<Result<ChatFileUploadResponse.FileUploadData>>


    fun uploadGroupImageFile(
        file: File,
        id: Int
    ): Flow<Result<String>>


    fun getGroupConversationMessageList(page: Int): Flow<Result<List<GroupChatSendMessageResponse.MessageDataItem>>>

    fun updateTaskMessage(id: Int, isDone: Boolean): Flow<Result<UpdateTaskMessageResponse.Data>>

    fun getReceiverData(): Flow<Result<AllWorkersResponse.DataItem>>

    fun getAllWorkers(): Flow<Result<List<AllWorkersShortDataResponse.WorkerShortDataItem>>>

    fun getTeamWorkers(): Flow<Result<List<AllWorkersShortDataResponse.WorkerShortDataItem>>>

    fun getGroupDetailData(): Flow<Result<GroupChatDetailResponse.ResponseData>>

    fun getGroupDetailUpdateData(title: String): Flow<Result<ResponseBody>>

    fun getGroupDetailUpdateDataWithIMg(title: String, img: File): Flow<Result<ResponseBody>>

}
