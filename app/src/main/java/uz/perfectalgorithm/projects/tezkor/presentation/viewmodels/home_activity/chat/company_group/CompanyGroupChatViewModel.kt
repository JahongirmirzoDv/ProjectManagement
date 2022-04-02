package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.company_group

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceNonDisposable
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.domvet.utils.models.response.local.ResultData
import uz.star.mardex.model.results.local.MessageData
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 30.10.2021 11:47
 **/

@HiltViewModel
class CompanyGroupChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val localStorage: LocalStorage,
    private val socketRepository: ChatSocketRepository
) : ViewModel() {

    var isOnlyWriteAdmin:Boolean=false
    var mChatList = ArrayList<ChatGroupListResponse.GroupChatDataItem>()

    private val _chatListLiveData =
        MutableLiveData<List<ChatGroupListResponse.GroupChatDataItem>>()
    val chatListLiveData: LiveData<List<ChatGroupListResponse.GroupChatDataItem>> get() = _chatListLiveData

    private val _progressLiveData = MutableLiveData<Boolean>(false)
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData

    fun getChatList() {
//        _progressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCompanyGroupChatList().collect {
//                _progressLiveData.postValue(false)
                it.onSuccess { list ->
                    _chatListLiveData.postValue(list)
                }

                it.onFailure { throwable ->
//                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    /**** Local Storage   **/
    fun getUserId(): Int = localStorage.userId

    fun setChatId(groupChatData: ChatGroupListResponse.GroupChatDataItem) {
        localStorage.chatId = groupChatData.id!!
        repository.currentGroupChatData = groupChatData
    }

//    fun setChatIdGroup(groupChatData: ChatGroupListResponse.GroupChatDataItem) {
//        localStorage.chatId = groupChatData.id!!
//        repository.currentGroupChatData = groupChatData
//    }

    /** With Socket**/
    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _createNewChatLiveData =
        MutableLiveData<PersonalChatListResponse.PersonalChatDataItem>()
    val createNewChatLiveData: LiveData<PersonalChatListResponse.PersonalChatDataItem> get() = _createNewChatLiveData

    private val _deleteChatLiveData = MutableLiveData<Int>()
    val deleteChatLiveData: LiveData<Int> get() = _deleteChatLiveData

    private val _sendMessageLiveData =
        MutableLiveData<GroupChatSendMessageResponse.MessageDataItem>()
    val sendMessageLiveData: LiveData<GroupChatSendMessageResponse.MessageDataItem> get() = _sendMessageLiveData


    private var newCreateChatLiveData: LiveData<ResultData<PersonalChatListResponse.PersonalChatDataItem>>? =
        null

    private var newErrorResponseLiveData: LiveData<ResultData<Throwable>>? =
        null

    private var newDeleteChatLiveData: LiveData<ResultData<Int>>? =
        null

    private var newSendMessageLiveData: LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>? =
        null

    fun connectAndComeResponses() {
        onChatCreateCome()
        onChatDeleteCome()
        onSendMessageResponseCome()
        onErrorResponseCome()
        socketRepository.connectSocket()
    }

    fun disconnectSocket() {
        try {
            newCreateChatLiveData = null
            newDeleteChatLiveData = null
            newSendMessageLiveData = null
        } catch (e: Exception) {
            myLogD("Error with disconnect WebSocket: $e")
        }
    }

    fun deleteChat(chatId: Int) {
//        _progressLiveData.value = true
        socketRepository.deleteGroupChat(chatId)
    }

    private fun onChatCreateCome() {
        newCreateChatLiveData = socketRepository.onCreatePersonalChatCome()
        _message.removeSource(newCreateChatLiveData!!)

        _message.addSourceNonDisposable(newCreateChatLiveData!!) {
            it.onData { newMessage ->
                _createNewChatLiveData.value = newMessage
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onChatDeleteCome() {
        newDeleteChatLiveData = socketRepository.onDeleteGroupChatCome()
        _message.removeSource(newDeleteChatLiveData!!)
        _message.addSourceNonDisposable(newDeleteChatLiveData!!) {
            it.onData { deletedId ->
//                _progressLiveData.value = false
                _deleteChatLiveData.value = deletedId
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
//                    _progressLiveData.value = false
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
//                _progressLiveData.value = false
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onSendMessageResponseCome() {
        newSendMessageLiveData = socketRepository.onSendMessageGroupChatCome()
        _message.removeSource(newSendMessageLiveData!!)

        _message.addSourceNonDisposable(newSendMessageLiveData!!) {
            it.onData { newMessage ->
//                _progressLiveData.value = false
                _sendMessageLiveData.value = newMessage

            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
//                    _progressLiveData.value = false
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
//                _progressLiveData.value = false
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onErrorResponseCome() {
        newErrorResponseLiveData = socketRepository.onErrorResponseCome()
        _message.removeSource(newErrorResponseLiveData!!)

        _message.addSourceNonDisposable(newErrorResponseLiveData!!) {
            it.onData { errorMessage ->
//                _progressLiveData.value = false
                _errorLiveData.value = errorMessage
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
//                    _progressLiveData.value = false
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
//                _progressLiveData.value = false
                _message.value = MessageData.message(string)
            }
        }
    }

}