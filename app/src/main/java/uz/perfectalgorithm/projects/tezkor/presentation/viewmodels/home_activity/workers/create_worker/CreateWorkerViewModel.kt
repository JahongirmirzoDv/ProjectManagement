package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.create_worker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.RoleEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.UserPermissionsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreatePositionRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.CreateWorkerResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.PermissionsListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.department.DepartmentRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateWorkerViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val departmentRepository: DepartmentRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private var mDepartments: List<DepartmentListResponse.DepartmentDataItem>? = null
    private var mPermissions: List<PermissionsListResponse.PermissionData>? = null
    private var mBelongsPermissions: List<String>? = null

    private val _createWorkerLiveData = MutableLiveData<CreateWorkerResponse.DataItem>()
    val createWorkerLiveData: LiveData<CreateWorkerResponse.DataItem> get() = _createWorkerLiveData

    private val _structureWithPositions =
        MutableLiveData<List<StructurePositionsResponse.DataItem>>()
    val structureWithPositionsLiveData: LiveData<List<StructurePositionsResponse.DataItem>> get() = _structureWithPositions

    private val _createPositionLiveData =
        MutableLiveData<CreatePositionResponse.PositionData>()
    val createPositionLiveData: LiveData<CreatePositionResponse.PositionData> get() = _createPositionLiveData

    private val _departmentListLiveData =
        MutableLiveData<List<DepartmentListResponse.DepartmentDataItem>>()
    val departmentListLiveData: LiveData<List<DepartmentListResponse.DepartmentDataItem>> get() = _departmentListLiveData

    private val _permissionsLiveData =
        MutableLiveData<List<PermissionsListResponse.PermissionData>>()
    val permissionsLiveData: LiveData<List<PermissionsListResponse.PermissionData>> get() = _permissionsLiveData

    private val _belongsToUserPermissionsLiveData =
        MutableLiveData<List<String>>()
    val belongsToUserPermissionsLiveData: LiveData<List<String>> get() = _belongsToUserPermissionsLiveData

    private val _errorDialogPositionLiveData = MutableLiveData<Throwable>()
    val errorDialogPositionLiveData: LiveData<Throwable> get() = _errorDialogPositionLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun createWorker(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        password: String,
        gender: String,
        positions: List<StructurePositionsResponse.PositionsItem>,
        isOutsource: Boolean
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.createWorker(
                firstName,
                lastName,
                dateOfBirth,
                phone,
                email,
                password,
                positions.getPositionIdList(),
                gender,
                isOutsource
            )
                .collect {
                    it.onSuccess { createResponse ->
                        _createWorkerLiveData.postValue(createResponse)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable)
                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

    fun createWorkerWithImage(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        password: String,
        positions: List<StructurePositionsResponse.PositionsItem>,
        gender: String,
        file: File,
        isOutsource: Boolean
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.createWorkerWithImage(
                firstName,
                lastName,
                dateOfBirth,
                phone,
                email,
                password,
                positions.getPositionIdList(),
                gender,
                file,
                isOutsource
            )
                .collect {
                    it.onSuccess { createResponse ->
                        _createWorkerLiveData.postValue(createResponse)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable)
                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

    fun getStructureWithPositionsData() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStructureWithPositions().collect {
                it.onSuccess { listProjects ->
                    _structureWithPositions.postValue(listProjects)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    private fun List<StructurePositionsResponse.PositionsItem>.getPositionIdList(): List<Int> {
        val ids = ArrayList<Int>()
        for (a in this) {
            a.id?.let { ids.add(it) }
        }
        return ids

    }

    var isOpenAddFunctions = true

    var canAdd = false
    var canAddUser = false
    var canAddDepartment = false
    var canAddPosition = false

    fun setCorrectForUIPermissions(permissions: List<String>) {
        /**
         * Har qanday hodim uchun yangi hodim, lavozim , bo'lim yaratish ruxsatini tekshirish funksiyasi,
         * shunga mos holatda qoshish button bosilganda chiqadigan actionlar aniqlangan
         * **/

        var c = 0
        permissions.forEach { a ->
            when (a) {
                UserPermissionsEnum.ADD_DEPARTMENT.text -> {
                    canAddDepartment = true
                    c++
                }
                UserPermissionsEnum.ADD_POSITION.text -> {
                    canAddPosition = true
                    c++
                }
                UserPermissionsEnum.ADD_USER.text -> {
                    canAddUser = true
                    c++
                }
            }
        }
        canAdd = c > 0

        if (storage.role == RoleEnum.OWNER.text) {
            canAdd = true
            canAddUser = true
            canAddPosition = true
            canAddDepartment = true
        }
    }

    fun setMPermissions(permissionList: List<PermissionsListResponse.PermissionData>) {
        mPermissions = permissionList
    }

    fun getMPermissions(): List<PermissionsListResponse.PermissionData>? {
        return mPermissions
    }

    fun setMDepartments(departmentList: List<DepartmentListResponse.DepartmentDataItem>) {
        mDepartments = departmentList
    }

    fun getMDepartments(): List<DepartmentListResponse.DepartmentDataItem>? {
        return mDepartments
    }

    fun createPosition(sendRequest: CreatePositionRequest) {

        val request =
            CreatePositionRequest(
                title = sendRequest.title,
                department = sendRequest.department,
                company = storage.selectedCompanyId,
                hierarchy = sendRequest.hierarchy,
                permissions = sendRequest.permissions
            )

        viewModelScope.launch(Dispatchers.IO) {
            repository.createPosition(request).collect {
                it.onSuccess { createResponse ->
                    _createPositionLiveData.postValue(createResponse)
                }

                it.onFailure { throwable ->
                    _errorDialogPositionLiveData.postValue(throwable)
                }
            }
        }
    }

    fun getDepartmentListData() {
//            _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            departmentRepository.getDepartmentList().collect {
                it.onSuccess { listProjects ->
                    _departmentListLiveData.postValue(listProjects)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun getUserPermissionsList() {
//            _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPermissionsList().collect {
                it.onSuccess { listPermissions ->
                    _permissionsLiveData.postValue(listPermissions)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }

            }
        }
    }

    fun getBelongsToUserPermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBelongsToUserPermissions().collect {
                it.onSuccess { listPermissions ->
                    _belongsToUserPermissionsLiveData.postValue(listPermissions)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}