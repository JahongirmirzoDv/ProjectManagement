package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers

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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.PermissionsListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreateDepartmentResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.department.DepartmentRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import javax.inject.Inject

/**
 * Craeted by Davronbek Raximjanov on 17.06.2021
 **/

@HiltViewModel
class WorkersViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val departmentRepository: DepartmentRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private var mDepartments: List<DepartmentListResponse.DepartmentDataItem>? = null
    private var mPermissions: List<PermissionsListResponse.PermissionData>? = null
    private var mBelongsPermissions: List<String>? = null

    var mUserData: UserDataResponse.Data = UserDataResponse.Data()

    var mWorkerList: List<AllWorkersResponse.DataItem> = ArrayList()

    fun setMBelongsPermissions(permissionList: List<String>) {
        mBelongsPermissions = permissionList
    }

    fun getMBelongsPermissions(): List<String>? {
        return mBelongsPermissions
    }

    var isOpenAddFunctions = true

    var canAdd = false
    var canAddUser = false
    var canAddDepartment = false
    var canAddPosition = false

    init {

    }

    fun setCorrectForUIPermissions(permissions: List<String>) {
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

        logDataForPerm()
    }

    fun logDataForPerm() {
        myLogD("can add __ = $canAdd", "LOG20")
        myLogD("can add department = $canAddDepartment", "LOG20")
        myLogD("can add position = $canAddPosition", "LOG20")
        myLogD("can add user = $canAddUser", "LOG20")
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

    private val _addUserToFavouritesLiveData = MutableLiveData<WorkerDataResponse.WorkerData>()
    val addUserToFavouritesLiveData: LiveData<WorkerDataResponse.WorkerData> get() = _addUserToFavouritesLiveData

    private val _removeFromFavouritesLiveData = MutableLiveData<WorkerDataResponse.WorkerData>()
    val removeFromFavouritesLiveData: LiveData<WorkerDataResponse.WorkerData> get() = _removeFromFavouritesLiveData

    private val _createDepartmentLiveData =
        MutableLiveData<CreateDepartmentResponse.DepartmentData>()
    val createDepartmentLiveData: LiveData<CreateDepartmentResponse.DepartmentData> get() = _createDepartmentLiveData

    private val _createPositionLiveData =
        MutableLiveData<CreatePositionResponse.PositionData>()
    val createPositionLiveData: LiveData<CreatePositionResponse.PositionData> get() = _createPositionLiveData

    private val _departmentListLiveData =
        MutableLiveData<List<DepartmentListResponse.DepartmentDataItem>>()
    val departmentListLiveData: LiveData<List<DepartmentListResponse.DepartmentDataItem>> get() = _departmentListLiveData

    private val _errorDialogLiveData = MutableLiveData<Throwable>()
    val errorDialogLiveData: LiveData<Throwable> get() = _errorDialogLiveData

    private val _errorDialogPositionLiveData = MutableLiveData<Throwable>()
    val errorDialogPositionLiveData: LiveData<Throwable> get() = _errorDialogPositionLiveData

    private val _userResponseLiveData = MutableLiveData<UserDataResponse.Data>()
    val userResponseLiveData: LiveData<UserDataResponse.Data> get() = _userResponseLiveData

    private val _allWorkersLiveData = MutableLiveData<List<AllWorkersResponse.DataItem>>()
    val allWorkersLiveData: LiveData<List<AllWorkersResponse.DataItem>> get() = _allWorkersLiveData

    private val _permissionsLiveData =
        MutableLiveData<List<PermissionsListResponse.PermissionData>>()
    val permissionsLiveData: LiveData<List<PermissionsListResponse.PermissionData>> get() = _permissionsLiveData

    private val _belongsToUserPermissionsLiveData =
        MutableLiveData<List<String>>()
    val belongsToUserPermissionsLiveData: LiveData<List<String>> get() = _belongsToUserPermissionsLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserData().collect {
                _progressLiveData.value = true
                it.onSuccess { userData ->
                    _userResponseLiveData.postValue(userData)
                    _progressLiveData.value = false
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun getAllWorkers() {
//            _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllWorkers().collect {

                it.onSuccess { allWorkers ->
                    _allWorkersLiveData.postValue(allWorkers)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
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

    fun createDepartment(title: String, parentId: Int) {
        if (parentId > 0) {
            val request = CreateDepartmentRequest(title, parentId)
            viewModelScope.launch(Dispatchers.IO) {
                departmentRepository.createDepartment(request).collect {
                    it.onSuccess { createResponse ->
                        _createDepartmentLiveData.postValue(createResponse)
                    }

                    it.onFailure { throwable ->
                        _errorDialogLiveData.postValue(throwable)
                    }
                }
            }
        } else {
            val request = CreatePrimaryDepartmentRequest(title)
            viewModelScope.launch(Dispatchers.IO) {
                departmentRepository.createPrimaryDepartment(request).collect {
                    it.onSuccess { createResponse ->
                        _createDepartmentLiveData.postValue(createResponse)
                    }

                    it.onFailure { throwable ->
                        _errorDialogLiveData.postValue(throwable)
                    }
                }
            }
        }
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

    fun addToFavourite(userId: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUserToFavourites(AddUserToFavouritesRequest(userId)).collect {
                it.onSuccess { staffData ->
                    _addUserToFavouritesLiveData.postValue(staffData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun removeFromFavourites(userId: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeUserFromFavourites(RemoveUserFromFavouritesRequest(userId))
                .collect {
                    it.onSuccess { staffData ->
                        _removeFromFavouritesLiveData.postValue(staffData)
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
