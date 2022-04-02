package uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo

import androidx.lifecycle.LiveData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat.AddMembersToGroupChatRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat.RemoveMembersFromGroupChatRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.star.domvet.utils.models.response.local.ResultData

/**
 * Created by Raximjanov Davronbek on 27.07.2021 19:05
 **/

interface ChatSocketRepository {

    /**Connect variables**/
    var isConnected: Boolean

    /** connect - disconnect functions */

    fun connectSocket()
    fun disconnectSocket()

    /** request functions */

    fun createPersonalChat(receiverId: Int)
    fun deletePersonalChat(chatId: Int)
    fun deleteGroupChat(chatId: Int)
    fun createGroupChat(title: String, members: List<Int>)

    fun sendMessage(chatId: Int, messageText: String, answerFor: Int?, files: List<Int>?)
    fun updateMessage(messageId: Int, newMessageText: String)
    fun updateMessageGroup(messageId: Int, newMessageText: String)
    fun deleteMessages(messageIds: List<Int>)

    /*Group*/
    fun sendMessageGroup(chatId: Int, messageText: String, answerFor: Int?, files: List<Int>?)
    fun deleteGroupMessages(messageIds: List<Int>)

    fun addMembersToGroup(chatId: Int, membersList: List<Int>)
    fun removeMembersFromGroup(chatId: Int, membersList: List<Int>)

    /** response listener functions */

    fun onCreatePersonalChatCome(): LiveData<ResultData<PersonalChatListResponse.PersonalChatDataItem>>
    fun onDeletePersonalChatCome(): LiveData<ResultData<Int>>

    fun onDeleteGroupChatCome(): LiveData<ResultData<Int>>


    fun onSendMessagePersonalChatCome(): LiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>>
    fun onUpdateMessagePersonalChatCome(): LiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>>
    fun onDeleteMessagesPersonalChatCome(): LiveData<ResultData<List<Int>>>

    fun onSendMessageGroupChatCome(): LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>
    fun onUpdateMessageGroupChatCome(): LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>
    fun onCreateGroupChatCome(): LiveData<ResultData<ChatGroupListResponse.GroupChatDataItem>>
    fun onDeleteMessagesGroupChatCome(): LiveData<ResultData<List<Int>>>

    fun onErrorResponseCome(): LiveData<ResultData<Throwable>>

}


