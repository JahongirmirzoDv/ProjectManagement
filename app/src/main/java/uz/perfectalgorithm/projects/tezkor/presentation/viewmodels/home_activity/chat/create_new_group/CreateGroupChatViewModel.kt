package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.create_new_group

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
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
 * Created by Davronbek Raximjanov on 07.10.2021 21:03
 **/

@HiltViewModel
class CreateGroupChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val localStorage: LocalStorage,
    private val socketRepository: ChatSocketRepository
) : ViewModel() {

    /** With Http **/

    val notConnectionLiveData = MutableLiveData<Unit>()

    private val _errorLiveData = MediatorLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _progressLiveData = MediatorLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _imageLiveData = MediatorLiveData<String>()
    val imageLiveData: LiveData<String> get() = _imageLiveData


    fun uploadFile(file: File, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadGroupImageFile(file = file, id = id).collect {
                it.onSuccess { image ->
                    _imageLiveData.postValue(image)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }


    /** Local Storage **/
    fun getUserId() = localStorage.userId
    fun getUserFullName() = "${localStorage.userFirstName} ${localStorage.userLastName}"

    fun setChatId(groupChatData: ChatGroupListResponse.GroupChatDataItem) {
        localStorage.chatId = groupChatData.id!!
        repository.currentGroupChatData = groupChatData
    }

    /** With Socket **/

    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _createNewChatLiveData =
        MutableLiveData<ChatGroupListResponse.GroupChatDataItem>()
    val createNewChatLiveData: LiveData<ChatGroupListResponse.GroupChatDataItem> get() = _createNewChatLiveData

    private val _uploadFileLiveData = MutableLiveData<ChatFileUploadResponse.FileUploadData>()
    val uploadFileLiveData: LiveData<ChatFileUploadResponse.FileUploadData> get() = _uploadFileLiveData

    private var newCreateChatLiveData: LiveData<ResultData<ChatGroupListResponse.GroupChatDataItem>>? =
        null

    private var newUpdateMessageLiveData: LiveData<ResultData<PersonalChatMessageListResponse.MessageDataItem>>? =
        null

    private var newErrorResponseLiveData: LiveData<ResultData<Throwable>>? =
        null


    fun connectSocketAndComeResponses() {
        onErrorResponseCome()
        onChatCreateCome()
        socketRepository.connectSocket()
    }

    fun disconnectSocket() {
        try {
            newErrorResponseLiveData = null
            socketRepository.disconnectSocket()
        } catch (e: Exception) {
            myLogD("Error with disconnect WebSocket: $e")
        }
    }


    /*REQUESTS*/

    fun createGroupChat(title: String, members: List<Int>) {
        _progressLiveData.value = true
        socketRepository.createGroupChat(title = title, members = members)
    }


    /*RESPONSES*/

    private fun onChatCreateCome() {
        newCreateChatLiveData = socketRepository.onCreateGroupChatCome()
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

