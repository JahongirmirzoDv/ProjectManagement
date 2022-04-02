package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.PackagePurchaseItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.AddDateData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.AddStaffData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.TariffData
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package.PackageRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 8/24/2021 11:57 AM
 **/
@HiltViewModel
class PackagePurchaseViewModel @Inject constructor(
    private val packageRepository: PackageRepository
) : ViewModel() {

    private val _getTariffPlanList = MutableLiveData<TariffData>()
    val getTariffPlanList: LiveData<TariffData> get() = _getTariffPlanList

    private val _packageLiveData = MutableLiveData<PackagePurchaseItem>()
    val packageLiveData: LiveData<PackagePurchaseItem> get() = _packageLiveData

    private val _staffLiveData = MutableLiveData<AddStaffData>()
    val staffLiveData: LiveData<AddStaffData> get() = _staffLiveData

    private val _monthLiveData = MutableLiveData<AddDateData>()
    val monthLiveData: LiveData<AddDateData> get() = _monthLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

 /*   init {
        getTariffPlans()
    }*/

    fun buyPackage(count: Int, month: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.buyPackage(count, month).collect {
                it.onSuccess { response ->
                    _packageLiveData.postValue(response.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }

    fun buyStaff(count: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.addStaff(count).collect {
                it.onSuccess { response ->
                    _staffLiveData.postValue(response.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }

    fun buyMonth(month: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.addDate(month).collect {
                it.onSuccess { response ->
                    _monthLiveData.postValue(response.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }

    fun getTariffPlans() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.getTariffPlans().collect {
                it.onSuccess { response ->
                    _getTariffPlanList.postValue(response.data!!)
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