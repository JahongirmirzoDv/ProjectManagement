package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.offers.addOffers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BasePostOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints.ComplaintsRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddOffersViewModel @Inject constructor(
    private val complaintsRepository: ComplaintsRepository
) : ViewModel() {

//    var job: Job? = null

    var whoFrom: PersonData? = null
    var participants = mutableListOf<Int>()
    var spectators = mutableListOf<PersonData>()
    var filesCount = 0


    private val _postOffer =
        MutableLiveData<Event<BasePostOfferResponse.DataResult>>()
    val postOffer: LiveData<Event<BasePostOfferResponse.DataResult>> get() = _postOffer

    private val _postComplaint =
        MutableLiveData<Event<BasePostOfferResponse.DataResult>>()
    val postComplaint: LiveData<Event<BasePostOfferResponse.DataResult>> get() = _postComplaint


    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    private val _allWorkersLiveData = MutableLiveData<Event<List<AllWorkersResponse.DataItem>>>()
    val allWorkersLiveData: LiveData<Event<List<AllWorkersResponse.DataItem>>> get() = _allWorkersLiveData


    fun postOffer(
        description: String,
        files: List<File>,
        spectators: List<Int>,
        to: Int?,
        toCompany: Boolean
    ) {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            complaintsRepository.postOffer(description, files, spectators, to, toCompany)
                .collect { it ->
                    it.onSuccess {
                        _postOffer.postValue(Event(it))
                        _progressLiveData.postValue(Event(false))
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(Event(throwable))
                        _progressLiveData.postValue(Event(false))
                    }

                }
        }
    }

    fun postComplaint(
        description: String,
        files: List<File>,
        spectators: List<Int>,
        to: Int?,
        toCompany: Boolean
    ) {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            complaintsRepository.postComplaint(description, files, spectators, to, toCompany)
                .collect { it ->
                    it.onSuccess {
                        _postComplaint.postValue(Event(it))
                        _progressLiveData.postValue(Event(false))
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(Event(throwable))
                        _progressLiveData.postValue(Event(false))
                    }
                }
        }
    }

//    fun cancelCreateData() {
//        job?.cancel()
//    }

    fun getAllWorkers() {
        _progressLiveData.value = Event(true)
        viewModelScope.launch(Dispatchers.IO) {
            complaintsRepository.getAllWorkers().collect {

                it.onSuccess { allWorkers ->
                    _allWorkersLiveData.postValue(Event(allWorkers))
                    _progressLiveData.postValue(Event(false))
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(Event(throwable))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }
}