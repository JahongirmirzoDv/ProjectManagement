package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.group.group_chat_detail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatDetailResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.GroupChatSendMessageResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatMessageListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceNonDisposable
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.star.domvet.utils.models.response.local.ResultData
import uz.star.mardex.model.results.local.MessageData
import java.io.File
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 25.10.2021 22:36
 **/

@HiltViewModel
class GroupChatDetailViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val localStorage: LocalStorage,
    private val socketRepository: ChatSocketRepository
) : ViewModel() {

    private val _detailLiveData =
        MutableLiveData<GroupChatDetailResponse.ResponseData>()
    val detailLiveData: LiveData<GroupChatDetailResponse.ResponseData> get() = _detailLiveData

    private val _detailEditLiveData =
        MutableLiveData<ResponseBody>()
    val detailEditLiveData: LiveData<ResponseBody> get() = _detailEditLiveData

    private val _allWorkersLiveData =
        MutableLiveData<List<AllWorkersShortDataResponse.WorkerShortDataItem>>()
    val allWorkersLiveData: LiveData<List<AllWorkersShortDataResponse.WorkerShortDataItem>> get() = _allWorkersLiveData

    val mAllWorkers = ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>()
     val mMembers = ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>()

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData

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

    fun detailUpdateData(title: String) {
        _progressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getGroupDetailUpdateData(title).collect {

                it.onSuccess { awd ->
                    _detailEditLiveData.postValue(awd)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun detailUpdateDataWithImage(title: String, img: File) {
        _progressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getGroupDetailUpdateDataWithIMg(title, img).collect {

                it.onSuccess { awd ->
                    _detailEditLiveData.postValue(awd)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun getAllWorkers() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllWorkers().collect {
                it.onSuccess { listProjects ->
                    _allWorkersLiveData.postValue(listProjects)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun submitMembers(list: List<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        mMembers.clear()
        mMembers.addAll(list)
    }

    fun submitAllWorkers(list: List<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        mAllWorkers.clear()
        mAllWorkers.addAll(list)
    }

    fun getWorkersWithoutThisGroupChat(): List<AllWorkersShortDataResponse.WorkerShortDataItem> {
        val list = ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem>()
        mAllWorkers.forEach { inAll ->
            var isAdd = true
            mMembers.forEach { inMembers ->
                if (inAll.fullName == inMembers.fullName) {
                    isAdd = false
                }
            }

            if (isAdd) {
                list.add(inAll)
            }
        }
        return list
    }

    /** WebSocket**/

    private var newErrorResponseLiveData: LiveData<ResultData<Throwable>>? =
        null


    fun addMembers(members: List<Int>) {
        _progressLiveData.postValue(true)
        repository.currentGroupChatData.id?.let { socketRepository.addMembersToGroup(it, members) }
    }

    fun connectSocketAndComeResponses() {
        socketRepository.connectSocket()
    }

    fun disconnectSocket() {
        try {

            socketRepository.disconnectSocket()
        } catch (e: Exception) {
            myLogD("Error with disconnect WebSocket: $e")
        }
    }

}