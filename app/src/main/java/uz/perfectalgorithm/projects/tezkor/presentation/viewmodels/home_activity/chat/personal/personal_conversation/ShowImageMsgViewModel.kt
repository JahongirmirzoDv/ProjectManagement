package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.personal.personal_conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.chat_services.ChatServicesRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.addSourceDisposable
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 18.08.2021
 **/

@HiltViewModel
class ShowImageMsgViewModel @Inject constructor(
    private val repository: ChatServicesRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _downloadFileLiveData = MediatorLiveData<String>()
    val downloadFileLiveData: LiveData<String> get() = _downloadFileLiveData

    private val _progressLiveData = MediatorLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    fun downloadImage(url: String) {
        _progressLiveData.value = true
        _downloadFileLiveData.addSourceDisposable(
            repository.downloadFile(
                url
            )
        ) {
            _downloadFileLiveData.value = it
            _progressLiveData.value = false
        }
    }

    fun getUserId() = storage.userId

}