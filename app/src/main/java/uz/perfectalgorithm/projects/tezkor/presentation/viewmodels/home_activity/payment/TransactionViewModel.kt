package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TransactionData
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package.PackageRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 8/23/2021 3:50 PM
 **/
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val packageRepository: PackageRepository

) : ViewModel() {

    private val _transactionListLiveData = MutableLiveData<TransactionData>()
    val transactionListLiveData: LiveData<TransactionData> get() = _transactionListLiveData


    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    init {
        getTransactionList()
    }

    private fun getTransactionList() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.getTransactionList().collect {
                it.onSuccess { response ->
                    _transactionListLiveData.postValue(response.data!!)
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