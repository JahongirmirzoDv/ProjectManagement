package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.personal

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceNonDisposable
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.domvet.utils.models.response.local.ResultData
import uz.star.mardex.model.results.local.MessageData
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 12.07.2021 12:20
 **/

@HiltViewModel
class PersonalChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val socketRepository: ChatSocketRepository,
    private val localStorage: LocalStorage
) : ViewModel() {

    var isNewChatOpen = false

    var mChatList = ArrayList<PersonalChatListResponse.PersonalChatDataItem>()

    /**With Http**/
    private val _allWorkersLiveData = MutableLiveData<List<AllWorkersResponse.DataItem>>()
    val allWorkersLiveData: LiveData<List<AllWorkersResponse.DataItem>> get() = _allWorkersLiveData

    private val _progressLiveData = MediatorLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData

    private val _chatListLiveData =
        MutableLiveData<List<PersonalChatListResponse.PersonalChatDataItem>>()
    val chatListLiveData: LiveData<List<PersonalChatListResponse.PersonalChatDataItem>> get() = _chatListLiveData


    /**Functions**/
    fun getChatList() {
        _progressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPersonalChatList().collect {

                it.onSuccess { list ->
                    _chatListLiveData.postValue(list)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    /**** Local Storage   **/
    fun getUserId(): Int = localStorage.userId

    fun setChatId(chatId: Int, creator: PersonalChatMessageListResponse.Creator) {
        localStorage.chatId = chatId
        repository.receiverUserShortData = creator
    }

    /** With Socket**/
    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _createNewChatLiveData =
        MutableLiveData<PersonalChatListResponse.PersonalChatDataItem>()
    val createNewChatLiveData: LiveData<PersonalChatListResponse.PersonalChatDataItem> get() = _createNewChatLiveData

    private val _deleteChatLiveData = MutableLiveData<Int>()
    val deleteChatLiveData: LiveData<Int> get() = _deleteChatLiveData

    private val _sendMessageLiveData =
        MutableLiveData<PersonalChatMessageListResponse.MessageDataItem>()
    val sendMessageLiveData: LiveData<PersonalChatMessageListResponse.MessageDataItem> get() = _sendMessageLiveData


    private var newCreateChatLiveData: LiveData<ResultData<PersonalChatListResponse.PersonalChatDataItem>>? =
        null

    private var newErrorResponseLiveData: LiveData<ResultData<Throwable>>? =
        null

    private var newDeleteChatLiveData: LiveData<ResultData<Int>>? =
        null

    private var newSendMessageLiveData: LiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>>? =
        null


    fun connectAndComeResponses() {
        onChatCreateCome()
        onChatDeleteCome()
        onErrorResponseCome()
        onSendMessageResponseCome()
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
        _progressLiveData.value = true
        socketRepository.deletePersonalChat(chatId)
    }

    fun createNewChatItem(receiverId: Int) {
        socketRepository.createPersonalChat(receiverId)
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
        newDeleteChatLiveData = socketRepository.onDeletePersonalChatCome()
        _message.removeSource(newDeleteChatLiveData!!)
        _message.addSourceNonDisposable(newDeleteChatLiveData!!) {
            it.onData { deletedId ->

                _progressLiveData.value = false
                _deleteChatLiveData.value = deletedId
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
        newSendMessageLiveData = socketRepository.onSendMessagePersonalChatCome()
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
}