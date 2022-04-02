package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.choose_company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.CompaniesResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.save_company.UserDataWithCompanyResponse
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import javax.inject.Inject

/**
 * Craeted by Davronbek Raximjanov on 25.06.2021
 * **/

@HiltViewModel
class ChooseCompanyViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val storage: LocalStorage
) : ViewModel() {

    var isChosenCompany = false

    private var _companiesResponse =
        MutableStateFlow<DataWrapper<List<CompaniesResponse.CompanyData>>>(DataWrapper.Empty())
    val companiesResponse: StateFlow<DataWrapper<List<CompaniesResponse.CompanyData>>> =
        _companiesResponse.asStateFlow()

    private var _saveCompanyResponse =
        MutableStateFlow<DataWrapper<UserDataWithCompanyResponse.UserWData>>(DataWrapper.Empty())
    val saveCompanyResponse: StateFlow<DataWrapper<UserDataWithCompanyResponse.UserWData>> =
        _saveCompanyResponse.asStateFlow()

    private val _chooseCompanyId = MutableStateFlow<Int?>(null)
    val chooseCompanyId: StateFlow<Int?> = _chooseCompanyId.asStateFlow()

    private val _chooseCompanyName = MutableStateFlow<String?>(null)
    val chooseCompanyName: StateFlow<String?> = _chooseCompanyName.asStateFlow()

    fun getCompaniesData() = viewModelScope.launch(Dispatchers.IO) {
        _companiesResponse.value = DataWrapper.Loading()
        _companiesResponse.value = repository.getCompaniesNew()
    }

    fun saveCompany(company: CompaniesResponse.CompanyData) =
        viewModelScope.launch(Dispatchers.IO) {
            _saveCompanyResponse.value = DataWrapper.Loading()
            _saveCompanyResponse.value = repository.saveCompanyToUserNew(company.id!!)
        }

    fun chooseAndSaveCompany(companyId: Int) {
        storage.selectedCompanyId = companyId
        storage.isChosenCompany = true
        _chooseCompanyId.value = companyId
    }

    fun chooseAndSaveCompanyName(companyName: String) {
        storage.selectedCompanyName = companyName
        storage.isChosenCompany = true
        _chooseCompanyName.value = companyName
    }

    fun chooseNoSaveCompany(companyId: Int) {
        storage.selectedCompanyId = companyId
        storage.isChosenCompany = false
        _chooseCompanyId.value = companyId
    }

    //    private val _companiesLiveData =
//        MutableLiveData<Event<ArrayList<CompaniesResponse.CompanyData>>>()
//    val companiesLiveData: LiveData<Event<ArrayList<CompaniesResponse.CompanyData>>> get() = _companiesLiveData
//
//    private val _progressLiveData = MutableLiveData<Boolean>()
//    val progressLiveData: LiveData<Boolean> get() = _progressLiveData
//
//    private val _errorLiveData = MutableLiveData<String>()
//    val errorLiveData: LiveData<String> get() = _errorLiveData
//
//    private val _notConnectionLiveData = MutableLiveData<Unit>()
//    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData
//
//    private val _chooseCompanyLiveData = MutableLiveData<Int>()
//    val chooseCompanyLiveData: MutableLiveData<Int> get() = _chooseCompanyLiveData
//
//    private val _saveCompanyToUserLiveData =
//        MutableLiveData<UserDataWithCompanyResponse.UserWData>()
//    val saveCompanyToUserLiveData: MutableLiveData<UserDataWithCompanyResponse.UserWData> get() = _saveCompanyToUserLiveData

//    fun getCompaniesData() {
//        if (isConnected()) {
//            _progressLiveData.value = true
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.getCompanies().collect {
//                    _progressLiveData.postValue(false)
//                    it.onSuccess { companiesData ->
//                        _companiesLiveData.postValue(Event(companiesData as ArrayList<CompaniesResponse.CompanyData>))
//                    }
//                    it.onFailure { throwable ->
//                        _errorLiveData.postValue(throwable.message)
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Unit
//    }

//    fun chooseAndSaveCompany(companyId: Int) {
//        storage.selectedCompanyId = companyId
//        storage.isChosenCompany = true
//        _chooseCompanyLiveData.value = companyId
//    }
//
//    fun chooseNoSaveCompany(companyId: Int) {
//        storage.selectedCompanyId = companyId
//        storage.isChosenCompany = false
//        _chooseCompanyLiveData.value = companyId
//    }

//    fun saveCompany(company: CompaniesResponse.CompanyData) {
//        if (isConnected()) {
//            _progressLiveData.value = true
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.saveCompanyToUser(company.id!!).collect {
//                    _progressLiveData.postValue(false)
//                    it.onSuccess { companiesData ->
//                        _saveCompanyToUserLiveData.postValue(companiesData)
//                    }
//                    it.onFailure { throwable ->
//                        _errorLiveData.postValue(throwable.message)
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Unit
//    }
}