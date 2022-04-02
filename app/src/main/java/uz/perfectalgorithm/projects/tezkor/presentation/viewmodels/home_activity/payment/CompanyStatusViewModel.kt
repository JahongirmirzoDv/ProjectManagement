package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.PackageItemData
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package.PackageRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 8/21/2021 9:45 PM
 **/

@HiltViewModel
class CompanyStatusViewModel @Inject constructor(
    private val packageRepository: PackageRepository
) : ViewModel() {

    private val _packageLiveData = MutableLiveData<PackageItem>()
    val packageLiveData: LiveData<PackageItem> get() = _packageLiveData

    private val _purchasedPackageList = MutableLiveData<List<PackageItemData>>()
    val purchasedPackageList: LiveData<List<PackageItemData>> get() = _purchasedPackageList

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    /*init {
        getPackage()
    }*/

    fun getPackage() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.getPackage().collect {
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

    fun getPurchasedPackageList() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.getPurchasedPackageList().collect {
                it.onSuccess { response ->
                    _purchasedPackageList.postValue(response.data!!)
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
