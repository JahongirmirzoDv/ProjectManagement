package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.edit_worker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.PositionsStateEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.UpdateUserDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.EditWorkerRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UpdateUserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.EditWorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.positons.AllPositionsResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.others.OthersRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import java.io.File
import javax.inject.Inject



@HiltViewModel
class EditWorkerViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val storage: LocalStorage
) : ViewModel() {

    var workerData = AllWorkersResponse.DataItem()

    var allStatPositions = ArrayList<AllPositionsResponse.PositionDataItem>()

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _editWorkerLiveData = MutableLiveData<EditWorkerDataResponse.Data>()
    val editWorkerLiveData: LiveData<EditWorkerDataResponse.Data> get() = _editWorkerLiveData

    private val _positionListLiveData =
        MutableLiveData<List<AllPositionsResponse.PositionDataItem>>()
    val positionListLiveData: LiveData<List<AllPositionsResponse.PositionDataItem>> get() = _positionListLiveData

    fun setAllPositionsStat(
        positions: List<AllPositionsResponse.PositionDataItem>,
        callBack: SingleBlock<List<AllPositionsResponse.PositionDataItem>>
    ) {
        var tempList = ArrayList<AllPositionsResponse.PositionDataItem>()
        tempList.addAll(positions)

        var i = 0
        tempList.forEach { allPItem ->
            workerData.position?.forEach ret1@{ oldPItem ->
                if (allPItem.id == oldPItem?.id) {
                    tempList[i].isOld = true
                    tempList[i].state = PositionsStateEnum.ADDED.text
                    return@ret1
                }
            }
            i++
        }

        allStatPositions.clear()
        allStatPositions.addAll(tempList)
        callBack.invoke(allStatPositions)
    }

    fun getPositionList() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPositionList().collect {
                it.onSuccess { positions ->
                    _positionListLiveData.postValue(positions)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun editWorkerData(
        firstName: String,
        lastName: String,
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val editWorkerRequest = EditWorkerRequest(
                firstName = firstName,
                lastName = lastName,
                newPositions = getNewAddedPositions(),
                removedPositions = getRemovePositions()
            )
            repository.editWorkerData(id = workerData.id!!, editWorkerRequest = editWorkerRequest)
                .collect {
                    it.onSuccess { editWorkerData ->
                        _editWorkerLiveData.postValue(editWorkerData)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable)
                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

    fun changePositionState(a: AllPositionsResponse.PositionDataItem) {
        for ((i, pos) in allStatPositions.withIndex()) {
            if (pos.id == a.id) {
                if (a.state == PositionsStateEnum.ADDED.text) {
                    allStatPositions[i].state = PositionsStateEnum.DELETED.text
                } else {
                    allStatPositions[i].state = PositionsStateEnum.ADDED.text
                }
                return
            }
        }
    }

    private fun getNewAddedPositions(): List<Int> {
        var list = ArrayList<Int>()
        for (a in allStatPositions) {
            if ((!a.isOld) && a.state == PositionsStateEnum.ADDED.text) {
                list.add(a.id!!)
            }
        }
        return list
    }

    private fun getRemovePositions(): List<Int> {
        var list = ArrayList<Int>()
        for (a in allStatPositions) {
            if ((a.isOld) && a.state == PositionsStateEnum.DELETED.text) {
                list.add(a.id!!)
            }
        }
        return list
    }

}