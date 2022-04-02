package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreatePrimaryDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.domain.home.department.DepartmentRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject

/**
 * Craeted by Davronbek Raximjanov on 21.06.2021
 * **/

@HiltViewModel
class StructureViewModel @Inject constructor(
    private val workerRepository: WorkersRepository,
    private val departmentRepository: DepartmentRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private var mDepartments: List<DepartmentListResponse.DepartmentDataItem>? = null


    fun setMDepartments(departmentList: List<DepartmentListResponse.DepartmentDataItem>) {
        mDepartments = departmentList
    }

    fun getMDepartments(): List<DepartmentListResponse.DepartmentDataItem>? {
        return mDepartments
    }

    private val _structureLiveData = MutableLiveData<List<DepartmentStructureShort>>()
    val structureLiveData: LiveData<List<DepartmentStructureShort>> get() = _structureLiveData

    private val _updateDepartmentLiveData = MutableLiveData<Unit>()
    val updateDepartmentLiveData: LiveData<Unit> get() = _updateDepartmentLiveData

    private val _deleteDepartmentLiveData = MutableLiveData<Unit>()
    val deleteDepartmentLiveData: LiveData<Unit> get() = _deleteDepartmentLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _deleteWorkerLiveData = MutableLiveData<Unit>()
    val deleteWorkerLiveData: LiveData<Unit> get() = _deleteWorkerLiveData

    fun getStructureData() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            workerRepository.getStructureShortFlow().collect {
//            workerRepository.getStructure().collect {
                it.onSuccess { listProjects ->
                    _structureLiveData.postValue(listProjects)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun deleteWorker(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            workerRepository.deleteWorker(id).collect {
                it.onSuccess { _unit ->
                    _deleteWorkerLiveData.postValue(_unit)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun deleteDepartment(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            departmentRepository.deleteDepartment(id).collect {
                it.onSuccess {
                    _deleteDepartmentLiveData.postValue(Unit)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun updateDepartment(id: Int, title: String) {
        _progressLiveData.value = true

        viewModelScope.launch(Dispatchers.IO) {
            departmentRepository.updateDepartment(id, CreatePrimaryDepartmentRequest(title))
                .collect {
                    it.onSuccess {
                        _updateDepartmentLiveData.postValue(Unit)
                        _progressLiveData.postValue(false)
                    }

                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable)
                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

}