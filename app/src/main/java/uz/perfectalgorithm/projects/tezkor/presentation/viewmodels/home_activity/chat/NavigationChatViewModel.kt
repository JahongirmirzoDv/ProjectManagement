package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.socket_repo.ChatSocketRepository
import uz.perfectalgorithm.projects.tezkor.utils.constants_chat.SOCKET_STATUS_TAG
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 16.06.2021 18:10
 **/

@HiltViewModel
class NavigationChatViewModel @Inject constructor(
    private val repository: ChatSocketRepository
) : ViewModel() {

    private val _openQuickIdeasScreen = MutableLiveData<Unit>()
    val openQuickIdeasScreen: LiveData<Unit> get() = _openQuickIdeasScreen

    private val _openQuickPlansScreen = MutableLiveData<Unit>()
    val openQuickPlansScreen: LiveData<Unit> get() = _openQuickPlansScreen

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData

    init {
//        connectSocket()
    }

    fun openQuickIdeas() {
        _openQuickIdeasScreen.value = Unit
    }

    fun openQuickPlans() {
        _openQuickPlansScreen.value = Unit
    }

    fun disconnectSocket() {
        try {
            repository.disconnectSocket()
            Log.d("T12T", "WEBSOCKET disconnected !!!")
            myLogD("WEBSOCKET disconnected !!!: VM", SOCKET_STATUS_TAG)
        } catch (e: Exception) {
            myLogD("ERROR with disconnect !!!: VM ", SOCKET_STATUS_TAG)
        }
    }

    fun connectSocket() {
        if (!repository.isConnected) {
            repository.connectSocket()
            myLogD("WEBSOCKET connected !!! :VM", SOCKET_STATUS_TAG)
        }
    }

}