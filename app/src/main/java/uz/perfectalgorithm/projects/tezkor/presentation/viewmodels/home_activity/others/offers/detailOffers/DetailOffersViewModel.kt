package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.offers.detailOffers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints.ComplaintsRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject


@HiltViewModel
class DetailOffersViewModel @Inject constructor(
    private val repository: ComplaintsRepository
) : ViewModel() {

    private val _getOfferLiveData =
        MutableLiveData<Event<BaseOffersItemResponse.DataResult>>()
    val getOfferLiveData: LiveData<Event<BaseOffersItemResponse.DataResult>> get() = _getOfferLiveData

    private val _getComplaintLiveData =
        MutableLiveData<Event<BaseOffersItemResponse.DataResult>>()
    val getComplaintLiveData: LiveData<Event<BaseOffersItemResponse.DataResult>> get() = _getComplaintLiveData


    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    private val _downloading: MutableLiveData<Boolean> = MutableLiveData()
    val downloading: LiveData<Boolean> = _downloading

    private val _deleteOffer =
        MutableLiveData<Boolean>()
    val deleteOffer: LiveData<Boolean> get() = _deleteOffer


    private val _deleteComplaint =
        MutableLiveData<Boolean>()
    val deleteComplaint: LiveData<Boolean> get() = _deleteComplaint


    fun setDownloading(downloading: Boolean) {
        _downloading.value = downloading
    }

    fun getOffer(
        id: Int
    ) {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            repository.getOfferItem(id).collect { it ->
                it.onSuccess {
                    _getOfferLiveData.postValue(Event(it))
                    _progressLiveData.postValue(Event(false))
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(Event(throwable))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }


    fun getComplaint(
        id: Int
    ) {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            repository.getComplaintItem(id).collect { it ->
                it.onSuccess {
                    _getComplaintLiveData.postValue(Event(it))
                    _progressLiveData.postValue(Event(false))
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(Event(throwable))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }

    fun deleteOffer(id: Int) {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            repository.deleteOffer(id).collect {
                it.onSuccess { it ->
                    _deleteOffer.postValue(it)
                    _progressLiveData.postValue(Event(false))
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(Event(throwable))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }

    fun deleteComplaint(id: Int) {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            repository.deleteComplaint(id).collect {
                it.onSuccess { it ->
                    _deleteComplaint.postValue(it)
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