package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.offers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOfferResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints.ComplaintsRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val complaintsRepository: ComplaintsRepository
) : ViewModel() {


    private val _getAllOffersAndComplaints =
        MutableLiveData<Event<List<BaseOfferResponse.DataResult>>>()
    val getAllOffersAndComplaints: LiveData<Event<List<BaseOfferResponse.DataResult>>> get() = _getAllOffersAndComplaints

    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData


    fun getOffersAndComplaints() {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            complaintsRepository.getOffersAndComplaints().collect { it ->
                it.onSuccess {
                    _getAllOffersAndComplaints.postValue(Event(it))
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