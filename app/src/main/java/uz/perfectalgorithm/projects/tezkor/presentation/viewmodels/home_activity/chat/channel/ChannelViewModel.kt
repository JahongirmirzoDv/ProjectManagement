package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.channel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelData
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.channel.ChannelRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 10:39
 **/

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _channelsLiveData = MutableLiveData<List<ChannelData>>()
    val channelsLiveData: LiveData<List<ChannelData>> get() = _channelsLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData


    fun getChannels() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getChannels().collect {
                it.onSuccess { listProjects ->
                    _channelsLiveData.postValue(listProjects)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}