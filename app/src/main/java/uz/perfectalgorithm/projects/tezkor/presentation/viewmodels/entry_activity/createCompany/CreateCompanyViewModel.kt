package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.createCompany

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.DirectionResponse
import uz.perfectalgorithm.projects.tezkor.domain.entry.company.CompanyRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateCompanyViewModel @Inject constructor(private val repository: CompanyRepository) :
    ViewModel() {

    private var _createCompanyResponse =
        MutableStateFlow<DataWrapper<Unit>>(DataWrapper.Empty())
    val createCompanyResponse: StateFlow<DataWrapper<Unit>> =
        _createCompanyResponse.asStateFlow()

    private var _directionResponse =
        MutableStateFlow<DataWrapper<List<DirectionResponse.Direction>>>(DataWrapper.Empty())
    val directionResponse: StateFlow<DataWrapper<List<DirectionResponse.Direction>>> =
        _directionResponse.asStateFlow()

    private var _directionResponse2 =
        MutableStateFlow<DataWrapper<List<DirectionResponse.Direction>>>(DataWrapper.Empty())
    val directionResponse2: StateFlow<DataWrapper<List<DirectionResponse.Direction>>> =
        _directionResponse2.asStateFlow()

    fun createCompany(
        title: String,
        image: File?,
        direction: String,
        username: String,
        email: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _createCompanyResponse.value = DataWrapper.Loading()
        _createCompanyResponse.value =
            repository.createCompanyNew(title, image, direction, username, email)
    }

    fun getCompaniesData() = viewModelScope.launch(Dispatchers.IO) {
        _directionResponse.value = DataWrapper.Loading()
        _directionResponse.value = repository.getDirection()
    }

    fun getCompaniesData2(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _directionResponse2.value = DataWrapper.Loading()
        _directionResponse2.value = repository.getDirection2(id)
    }

//    private val _createCompanyLiveData =
//        MutableLiveData<Event<Unit>>()
//    val createCompanyLiveData: LiveData<Event<Unit>> get() = _createCompanyLiveData
//
//    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
//    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData
//
//    private val _errorLiveData = MutableLiveData<Event<String>>()
//    val errorLiveData: LiveData<Event<String>> get() = _errorLiveData
//
//    private val _notConnectionLiveData = MutableLiveData<Event<Unit>>()
//    val notConnectionLiveData: LiveData<Event<Unit>> get() = _notConnectionLiveData

//    fun createCompany(
//        title: String,
//        image: File?,
//        direction: String,
//        username: String,
//        email: String
//    ) {
//        if (isConnected()) {
//            _progressLiveData.value = Event(true)
//
//            viewModelScope.launch {
//                repository.createCompany(title, image, direction, username, email).collect {
//                    _progressLiveData.value = Event(false)
//                    it.onSuccess {
//                        _createCompanyLiveData.postValue(Event(Unit))
//                    }
//                    it.onFailure { throwable ->
//                        _errorLiveData.postValue(Event(throwable.message ?: "Error"))
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Event(Unit)
//    }
}