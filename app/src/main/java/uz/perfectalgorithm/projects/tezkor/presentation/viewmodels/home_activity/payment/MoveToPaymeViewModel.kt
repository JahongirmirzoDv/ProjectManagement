package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.UrlData
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.PaymentRepository
import javax.inject.Inject


@HiltViewModel
class MoveToPaymeViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _moveToPaymeUrlLiveData = MutableLiveData<UrlData>()
    val moveToPaymeUrlLiveData: LiveData<UrlData> get() = _moveToPaymeUrlLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun getPaymentUrl(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            paymentRepository.paymentUrl(/*OrderIdBody(id)*/id).collect {
                it.onSuccess {
                    _moveToPaymeUrlLiveData.postValue(it.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }
}