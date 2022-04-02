package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.compass.compass_conversion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.firebase.response.MessageModel
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.compass.CompassChatRepository
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 22.07.2021 17:21
 **/

@HiltViewModel
class CompassConversationViewModel @Inject constructor(
    private val repository: CompassChatRepository,
    private val localStorage: LocalStorage
) : ViewModel() {

    private val _messagesListLiveData =
        MediatorLiveData<List<MessageModel>>()
    val messagesListLiveData: LiveData<List<MessageModel>> get() = _messagesListLiveData

    fun getMessageList() {

    }

    fun getReceiverId() = repository.receiverUser

    fun getUserId() = localStorage.userId

    fun testFirebase(string: String) {
        repository.testFirebase(string) {

        }
    }

}