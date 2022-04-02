package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.workers_.team_chat

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
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceNonDisposable
import uz.star.domvet.utils.models.response.local.ResultData
import uz.star.mardex.model.results.local.MessageData
import javax.inject.Inject


/**
 * Craeted by Davronbek Raximjanov on 20.08.2021
 * **/

@HiltViewModel
class TeamForChatViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val storage: LocalStorage,
    private val repositoryCh: ChatRepository,
    private val socketRepository: ChatSocketRepository
) : ViewModel() {

    var isNewChatOpen = false

    /** With Http **/
    private val _teamWorkersLiveData = MutableLiveData<List<AllWorkersResponse.DataItem>>()
    val teamWorkersLiveData: LiveData<List<AllWorkersResponse.DataItem>> get() = _teamWorkersLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun getTeamWorkers() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTeamWorkers().collect {
                it.onSuccess { listProjects ->
                    _teamWorkersLiveData.postValue(listProjects)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }


    /** With Socket**/
    private val _message = MediatorLiveData<MessageData>()
    val message: LiveData<MessageData> get() = _message

    private val _createNewChatLiveData =
        MutableLiveData<PersonalChatListResponse.PersonalChatDataItem>()
    val createNewChatLiveData: LiveData<PersonalChatListResponse.PersonalChatDataItem> get() = _createNewChatLiveData

    private var newCreateChatLiveData: LiveData<ResultData<PersonalChatListResponse.PersonalChatDataItem>>? =
        null

    private var newErrorResponseLiveData: LiveData<ResultData<Throwable>>? =
        null

    fun connectSocketAndComeResponses() {
        onChatCreateCome()
        onErrorResponseCome()
        socketRepository.connectSocket()
    }

    fun disconnectSocket() {
        try {
            socketRepository.disconnectSocket()
            newCreateChatLiveData = null
            newErrorResponseLiveData = null
        } catch (e: Exception) {
        }
    }

    fun createNewChatItem(receiverId: Int) {
        socketRepository.createPersonalChat(receiverId)
    }

    private fun onChatCreateCome() {
        newCreateChatLiveData = socketRepository.onCreatePersonalChatCome()
        _message.removeSource(newCreateChatLiveData!!)

        _message.addSourceNonDisposable(newCreateChatLiveData!!) {
            it.onData { newChat ->
                _createNewChatLiveData.postValue(newChat)
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


    /** Local Storage **/
    fun getUserId(): Int = storage.userId


    fun setChatId(chatId: Int, receiver: PersonalChatMessageListResponse.Creator) {
        storage.chatId = chatId
        repositoryCh.receiverUserShortData = receiver
    }


    /*fun createNewChatItem(receiverId: Int) {
        personalChatRepository.createChat(receiverId)
    }

    private fun onChatCreateCome() {
        newChatItemLiveData = personalChatRepository.onCreateChatItemCome()
        _message.removeSource(newChatItemLiveData!!)

        _message.addSourceNonDisposable(newChatItemLiveData!!) {
            it.onData { newMessage ->
                _createNewChatLiveData.value = Event(newMessage)
            }.onMessageData { messageData ->
                messageData.onResource { stringRes ->
                    _message.value = MessageData.resource(stringRes)
                }
            }.onMessage { string ->
                _message.value = MessageData.message(string)
            }
        }
    }

    fun connectAndGetChatList() {
        connectSocket()
        onChatCreateCome()
    }

    private fun connectSocket() {
        personalChatRepository.connectSocket()
    }

    fun disconnectSocket() {
        try {
            personalChatRepository.disconnectSocket()
            newChatItemLiveData = null
        } catch (e: Exception) {
        }
    }*/


}