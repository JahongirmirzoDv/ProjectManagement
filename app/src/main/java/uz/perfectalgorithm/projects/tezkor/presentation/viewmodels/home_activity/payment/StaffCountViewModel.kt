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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.StaffItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package.UserActivateResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.payment.company_package.PackageRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 06.08.2021 16:51
 **/
@HiltViewModel
class StaffCountViewModel @Inject constructor(
    private val packageRepository: PackageRepository

) : ViewModel() {


    private val _staffListLiveData = MutableLiveData<List<StaffItem>>()
    val staffListLiveData: LiveData<List<StaffItem>> get() = _staffListLiveData

    private val _userActivateLiveData = MutableLiveData<UserActivateResponse>()
    val userActivateLiveData: LiveData<UserActivateResponse> get() = _userActivateLiveData


    private val _packageLiveData = MutableLiveData<PackageItem>()
    val packageLiveData: LiveData<PackageItem> get() = _packageLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    init {
        getStaffList()
        getPackage()
    }

    private fun getStaffList() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.getStaffList().collect {
                it.onSuccess { response ->
                    _staffListLiveData.postValue(response.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }

    private fun getPackage() {
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

    fun userActivate(userId: Int, activeStatus: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            packageRepository.userActivate(/*ActiveDataBody(userId, activeStatus)*/userId,
                activeStatus
            ).collect {
                it.onSuccess { response ->
                    _userActivateLiveData.postValue(response)
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