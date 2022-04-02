/*

package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.personal.personal_conversation

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.ChatFileUploadResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.PersonalConversationMessagesResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages.UpdateTaskMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.personal.personal_coversation.PersonalConversationRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceDisposable
import uz.star.domvet.utils.models.response.local.ResultData
import uz.star.mardex.model.results.local.MessageData
import java.io.File
import javax.inject.Inject

*
 * Created by Davronbek Raximjanov on 12.07.2021 17:21
 *


@HiltViewModel
class DePersonalConversationViewModel @Inject constructor(
    private val repository: PersonalConversationRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _progress = MediatorLiveData<Boolean>()
    val progress: LiveData<Boolean> get() = _progress

    fun getUserId() = storage.userId

    private val _receiverUserLiveData = MutableLiveData<AllWorkersResponse.DataItem>()
    val receiverUserLiveData: LiveData<AllWorkersResponse.DataItem> get() = _receiverUserLiveData

    private val _uploadFileLiveData = MutableLiveData<ChatFileUploadResponse.FileUploadData>()
    val uploadFileLiveData: LiveData<ChatFileUploadResponse.FileUploadData> get() = _uploadFileLiveData

    private val _updateTaskMessageLiveData = MutableLiveData<UpdateTaskMessageResponse.Data>()
    val updateTaskMessageLiveData: LiveData<UpdateTaskMessageResponse.Data> get() = _updateTaskMessageLiveData

    private val _deleteMessagesLiveData = MediatorLiveData<List<Int>>()
    val deleteMessagesLiveData: LiveData<List<Int>> get() = _deleteMessagesLiveData

    private val _messagesListLiveData =
        MediatorLiveData<List<PersonalConversationMessagesResponse.MessageDataItem>>()
    val messagesListLiveData: LiveData<List<PersonalConversationMessagesResponse.MessageDataItem>> get() = _messagesListLiveData

    private val _messageLiveData =
        MediatorLiveData<PersonalConversationMessagesResponse.MessageDataItem>()
    val messageLiveData: LiveData<PersonalConversationMessagesResponse.MessageDataItem> get() = _messageLiveData

    private val _updateMessageLiveData =
        MediatorLiveData<PersonalConversationMessagesResponse.MessageDataItem>()
    val updateMessageLiveData: LiveData<PersonalConversationMessagesResponse.MessageDataItem> get() = _updateMessageLiveData

    private var newMessageLiveData: LiveData<ResultData<PersonalConversationMessagesResponse.MessageDataItem>>? =
        null

    private var newUpdateMessageLiveData: LiveData<ResultData<PersonalConversationMessagesResponse.MessageDataItem>>? =
        null

    private var newMessagesListLiveData: LiveData<ResultData<List<PersonalConversationMessagesResponse.MessageDataItem>>>? =
        null

    private var newDeleteMessagesLiveData: LiveData<ResultData<List<Int>>>? =
        null

    fun setUserFullName(fullName: String) {
        repository.userFullName = fullName
    }

    fun setReceiverFullName(fullName: String) {
        repository.userFullName = fullName
    }

    var currentReceiverUser: AllWorkersResponse.DataItem = AllWorkersResponse.DataItem()

    fun sendMessage(message: String, answerFor: Int) {
        repository.sendMessage(message, answerFor)
        _progress.value = true
    }

    fun updateMessage(newMessage: String, editedMessageId: Int) {
        repository.updateMessage(newMessage, editedMessageId)
        _progress.value = true
    }

    fun sendMessageFile(message: String, answerFor: Int, fileIDs: List<Int>) {
        repository.sendMessageWithImage(message, answerFor, fileIDs)
    }

    fun deleteMessages(messageIds: List<Int>) {
        repository.deleteMessages(messageIds)
    }

    fun getMessagesList(page: Int) {

        if (page <= repository.maxPage || repository.maxPage == 0) {
            repository.getMessagesList(page)
            _progress.value = true
        }

    }

    fun getMaxPage() = repository.maxPage

    fun uploadFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadFile(file).collect {
                it.onSuccess { createResponse ->
                    _uploadFileLiveData.postValue(createResponse)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable.message)
                }
            }
        }
    }

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
                _progress.value = false

                _messagesListLiveData.value = mList
            }.onMessage { message ->

                _progress.value = false
                _message.value = MessageData.message(message)
            }
        }
    }


    private fun onMessageCome() {
        newMessageLiveData = repository.onMessageCome()
        _message.removeSource(newMessageLiveData!!)

        _message.addSourceNonDisposable(newMessageLiveData!!) {
            _progress.value = false

            it.onData { newMessage ->
                _messageLiveData.value = newMessage
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _message.value = MessageData.message(string)
            }
        }
    }

    private fun onUpdateMessageCome() {
        newUpdateMessageLiveData = repository.onUpdateMessageCome()
        _message.removeSource(newUpdateMessageLiveData!!)

        _message.addSourceNonDisposable(newUpdateMessageLiveData!!) {
            _progress.value = false

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

    private fun onMessagesListCome() {
        newMessagesListLiveData = repository.onMessageListCome()
        _message.removeSource(newMessagesListLiveData!!)
        _message.addSourceNonDisposable(newMessagesListLiveData!!) {
            _progress.value = true

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
        _progress.value
        newDeleteMessagesLiveData = repository.onDeleteMessagesCome()
        _message.removeSource(newDeleteMessagesLiveData!!)
        _message.addSourceNonDisposable(newDeleteMessagesLiveData!!) {
            it.onData { deleteMessagesCount ->
                _progress.value = false
                _deleteMessagesLiveData.value = deleteMessagesCount
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _progress.value = false

                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _progress.value = false

                _message.value = MessageData.message(string)
            }
        }
    }

    fun connectWebSocket() {
        onMessageCome()
        onMessagesListCome()
        onDeleteMessagesCome()
        onUpdateMessageCome()

        connectSocket()
    }

    fun disconnectSocket() {
        try {
            repository.disconnectSocket()
            newMessageLiveData = null
            newMessagesListLiveData = null
            Log.d("T12T", "WEBSOCKET disconnected !!!")

        } catch (e: Exception) {
            Log.d("T12T", "ERROR WEBSOCKET!!!")
        }
    }

    private fun connectSocket() {
        repository.connectSocket()
    }

    fun userFirstName() = storage.userFirstName
    fun userLastName() = storage.userLastName

    fun getReceiverUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceiverUserData().collect {
                it.onSuccess { userData ->
                    _receiverUserLiveData.postValue(userData)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable.message)
                }
            }
        }
    }

}
*/
