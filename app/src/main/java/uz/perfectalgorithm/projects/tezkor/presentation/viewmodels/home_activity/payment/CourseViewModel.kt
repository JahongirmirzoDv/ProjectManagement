package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.OrderData
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.PaymentRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 8/20/2021 10:41 AM
 **/
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _courseLiveData = MutableLiveData<String>()
    val courseLiveData: LiveData<String> get() = _courseLiveData

    private val _orderLiveData = MutableLiveData<OrderData>()
    val orderLiveData: LiveData<OrderData> get() = _orderLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun getCourse(date: String) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            paymentRepository.getCourse(date).collect {
                it.onSuccess { userData ->
                    _courseLiveData.postValue(userData.rate!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }

    fun postOrder(dollarValue: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            paymentRepository.postOrder(dollarValue/*OrderBody(dollarValue)*/).collect {
                it.onSuccess { response ->
                    _progressLiveData.postValue(false)
                    _orderLiveData.postValue(response.data!!)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }


}