package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.group.group_conversation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.UpdateTaskMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceNonDisposable
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.domvet.utils.models.response.local.ResultData
import uz.star.mardex.model.results.local.MessageData
import java.io.File
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 07.10.2021 01:33
 **/

@HiltViewModel
class GroupConversationViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val storage: LocalStorage,
    private val socketRepository: ChatSocketRepository
) : ViewModel() {

    var page = 0

    var mMessageList: ArrayList<GroupChatSendMessageResponse.MessageDataItem> = ArrayList()

    /** With Http **/

    val notConnectionLiveData = MutableLiveData<Unit>()

    private val _errorLiveData = MediatorLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _progressLiveData = MediatorLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _detailLiveData =
        MutableLiveData<GroupChatDetailResponse.ResponseData>()
    val detailLiveData: LiveData<GroupChatDetailResponse.ResponseData> get() = _detailLiveData

    private val _messageListLiveData =
        MutableLiveData<List<GroupChatSendMessageResponse.MessageDataItem>>()
    val messageListLiveData: LiveData<List<GroupChatSendMessageResponse.MessageDataItem>> get() = _messageListLiveData

    fun getMessages() {
        _progressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            page++
            repository.getGroupConversationMessageList(page).collect {

                it.onSuccess { list ->
                    _messageListLiveData.postValue(list)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }
    fun isOwner() = storage.role == RoleEnum.OWNER.text
    fun getCurrentGroupData() = repository.currentGroupChatData

    fun getDetailData() {
        _progressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getGroupDetailData().collect {

                it.onSuccess { awd ->
                    _detailLiveData.postValue(awd)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun uploadFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadFile(file).collect {
                it.onSuccess { createResponse ->
                    _uploadFileLiveData.postValue(createResponse)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    /** Local Storage **/
    fun getUserId() = storage.userId
    fun getUserFullName() = "${storage.userFirstName} ${storage.userLastName}"

    /** With Socket **/

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _createNewChatLiveData =
        MutableLiveData<PersonalChatListResponse.PersonalChatDataItem>()
    val createNewChatLiveData: LiveData<PersonalChatListResponse.PersonalChatDataItem> get() = _createNewChatLiveData

    private val _deleteChatLiveData = MutableLiveData<Int>()
    val deleteChatLiveData: LiveData<Int> get() = _deleteChatLiveData

    private val _uploadFileLiveData = MutableLiveData<ChatFileUploadResponse.FileUploadData>()
    val uploadFileLiveData: LiveData<ChatFileUploadResponse.FileUploadData> get() = _uploadFileLiveData

    private val _updateTaskMessageLiveData = MutableLiveData<UpdateTaskMessageResponse.Data>()
    val updateTaskMessageLiveData: LiveData<UpdateTaskMessageResponse.Data> get() = _updateTaskMessageLiveData

    private val _deleteMessagesLiveData = MediatorLiveData<List<Int>>()
    val deleteMessagesLiveData: LiveData<List<Int>> get() = _deleteMessagesLiveData

    private val _updateMessageLiveData =
        MediatorLiveData<GroupChatSendMessageResponse.MessageDataItem>()
    val updateMessageLiveData: LiveData<GroupChatSendMessageResponse.MessageDataItem> get() = _updateMessageLiveData

    private val _sendMessageLiveData =
        MutableLiveData<GroupChatSendMessageResponse.MessageDataItem>()
    val sendMessageLiveData: LiveData<GroupChatSendMessageResponse.MessageDataItem> get() = _sendMessageLiveData


    private var newSendMessageLiveData: LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>? =
        null

    private var newUpdateMessageLiveData: LiveData<ResultData<GroupChatSendMessageResponse.MessageDataItem>>? =
        null

    private var newDeleteMessagesLiveData: LiveData<ResultData<List<Int>>>? =
        null

    private var newErrorResponseLiveData: LiveData<ResultData<Throwable>>? =
        null

    fun connectSocketAndComeResponses() {
        onErrorResponseCome()
        onSendMessageResponseCome()
        onUpdateMessageResponseCome()
        onDeleteMessagesCome()
        socketRepository.connectSocket()
    }

    fun disconnectSocket() {
        try {
            newSendMessageLiveData = null
            newDeleteMessagesLiveData = null
            newErrorResponseLiveData = null
            newUpdateMessageLiveData = null
            mMessageList.clear()
            socketRepository.disconnectSocket()

        } catch (e: Exception) {
            myLogD("Error with disconnect WebSocket: $e")
        }
    }

    private fun onErrorResponseCome() {
        newErrorResponseLiveData = socketRepository.onErrorResponseCome()
        _message.removeSource(newErrorResponseLiveData!!)

        _message.addSourceNonDisposable(newErrorResponseLiveData!!) {
            it.onData { errorMessage ->
                _progressLiveData.value = false
                _errorLiveData.value = errorMessage
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _progressLiveData.value = false
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _progressLiveData.value = false
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onSendMessageResponseCome() {
        newSendMessageLiveData = socketRepository.onSendMessageGroupChatCome()
        _message.removeSource(newSendMessageLiveData!!)

        _message.addSourceNonDisposable(newSendMessageLiveData!!) {
            it.onData { newMessage ->
                _progressLiveData.value = false
                _sendMessageLiveData.value = newMessage

            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _progressLiveData.value = false
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _progressLiveData.value = false
                _message.value = MessageData.message(string)
            }
        }
    }


    private fun onUpdateMessageResponseCome() {
        newUpdateMessageLiveData = socketRepository.onUpdateMessageGroupChatCome()
        _message.removeSource(newUpdateMessageLiveData!!)

        _message.addSourceNonDisposable(newUpdateMessageLiveData!!) {
            _progressLiveData.value = false

            it.onData { newMessage ->
                _updateMessageLiveData.value = newMessage
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onDeleteMessagesCome() {
        _progressLiveData.value
        newDeleteMessagesLiveData = socketRepository.onDeleteMessagesGroupChatCome()
        _message.removeSource(newDeleteMessagesLiveData!!)
        _message.addSourceNonDisposable(newDeleteMessagesLiveData!!) {
            it.onData { deleteMessagesCount ->
                _progressLiveData.value = false
                _deleteMessagesLiveData.value = deleteMessagesCount
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _progressLiveData.value = false

                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _progressLiveData.value = false

                _message.value = MessageData.message(string)
            }
        }
    }

    fun sendMessage(messageText: String, answerFor: Int?, files: List<Int>?) {
        socketRepository.sendMessageGroup(
            chatId = storage.chatId,
            messageText = messageText,
            answerFor = answerFor,
            files = files,
        )
        newSendMessageLiveData = null
        _progressLiveData.value = true
    }

    fun deleteMessages(messageIds: List<Int>) {
        socketRepository.deleteGroupMessages(messageIds)
        _progressLiveData.value = true
    }

    fun updateMessage(newMessage: String, editedMessageId: Int) {
        socketRepository.updateMessageGroup(editedMessageId, newMessage)
        _progressLiveData.value = true
    }


}

/*








    fun updateMessage(newMessage: String, editedMessageId: Int) {
        repository.updateMessage(newMessage, editedMessageId)
        _progressLiveData.value = true
    }

    fun sendMessageFile(message: String, answerFor: Int, fileIDs: List<Int>) {
        repository.sendMessageWithImage(message, answerFor, fileIDs)
    }



    fun getMessagesList(page: Int) {

        if (page <= repository.maxPage || repository.maxPage == 0) {
            repository.getMessagesList(page)
            _progressLiveData.value = true
        }

    }

    fun getMaxPage() = repository.maxPage


    fun updateTaskIsDone(isDone: Boolean, taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTaskMessage(id = taskId, isDone = isDone).collect {
                it.onSuccess { createResponse ->
                    _updateTaskMessageLiveData.postValue(createResponse)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable.message)
                }
            }
        }
    }


    fun getMessagesListLocal() {
        _messagesListLiveData.addSourceDisposable(repository.getMessagesListLocal()) { resultData ->
            resultData.onData { list ->
                if (list.isEmpty()) _message.value = MessageData.message("List bo'sh")
                var mList = ArrayList<PersonalConversationMessagesResponse.MessageDataItem>()
                list.forEach() { a ->
                    val messageM = PersonalConversationMessagesResponse.MessageDataItem(
                        id = a.id,
                        message = a.message,
                        messageType = a.messageType
                    )
                    mList.add(messageM)
                }
                _progressLiveData.value = false

                _messagesListLiveData.value = mList
            }.onMessage { message ->

                _progressLiveData.value = false
                _message.value = MessageData.message(message)
            }
        }
    }



    private fun onMessagesListCome() {
        newMessagesListLiveData = repository.onMessageListCome()
        _message.removeSource(newMessagesListLiveData!!)
        _message.addSourceNonDisposable(newMessagesListLiveData!!) {
            _progressLiveData.value = true

            it.onData { newMessage ->
                _messagesListLiveData.value = newMessage
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onDeleteMessagesCome() {
        _progressLiveData.value
        newDeleteMessagesLiveData = repository.onDeleteMessagesCome()
        _message.removeSource(newDeleteMessagesLiveData!!)
        _message.addSourceNonDisposable(newDeleteMessagesLiveData!!) {
            it.onData { deleteMessagesCount ->
                _progressLiveData.value = false
                _deleteMessagesLiveData.value = deleteMessagesCount
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _progressLiveData.value = false

                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _progressLiveData.value = false

                _message.value = MessageData.message(string)
            }
        }
    }




}*/
