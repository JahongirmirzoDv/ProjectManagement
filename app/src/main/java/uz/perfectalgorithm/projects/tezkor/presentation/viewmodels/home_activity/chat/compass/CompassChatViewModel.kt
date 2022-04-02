package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.compass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.ChatModel
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.compass.CompassChatRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 10:40
 **/
@HiltViewModel
class CompassChatViewModel @Inject constructor(
    private val repository: CompassChatRepository
) : ViewModel() {

    private val _chatsLiveData = MutableLiveData<List<ChatModel>>()
    val chatsLiveData: LiveData<List<ChatModel>> get() = _chatsLiveData

    fun getChatLIst() {
        repository.getAllChats { chatList ->
            _chatsLiveData.value = chatList
        }
    }

    fun setReceiver(messageId: AllWorkersResponse.DataItem) {

    }

    fun testFirebase(string: String) {
        repository.testFirebase(string) {
        }
    }


}