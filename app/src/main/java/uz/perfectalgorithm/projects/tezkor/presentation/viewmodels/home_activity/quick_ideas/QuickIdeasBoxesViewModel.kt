package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.IdeasBoxData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.UpdateQuickIdeasBoxData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepository
import javax.inject.Inject



@HiltViewModel
class QuickIdeasBoxesViewModel @Inject constructor(
    private val quickIdeasRepository: QuickIdeasRepository,
) : ViewModel() {


    private val _createIdeasBoxLiveData = MutableLiveData<IdeasBoxData>()
    val createIdeasBoxLiveData: LiveData<IdeasBoxData> get() = _createIdeasBoxLiveData

    private val _updateIdeasBoxLiveData = MutableLiveData<IdeasBoxData>()
    val updateIdeasBoxLiveData: LiveData<IdeasBoxData> get() = _updateIdeasBoxLiveData

    private val _deleteIdeasBoxLiveData = MutableLiveData<IdeasBoxData>()
    val deleteIdeasBoxLiveData: LiveData<IdeasBoxData> get() = _deleteIdeasBoxLiveData

    private val _allTaskFolderLiveData = MutableLiveData<List<TaskFolderListItem>>()
    val allTaskFolderLiveData: LiveData<List<TaskFolderListItem>> get() = _allTaskFolderLiveData

    private val _getAllIdeasBoxesLiveData = MutableLiveData<List<IdeasBoxData>>()
    val getAllIdeasBoxesLiveData: LiveData<List<IdeasBoxData>> get() = _getAllIdeasBoxesLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

/*    init {
        getAllIdeasBoxes()
    }*/


    fun createIdeasBox(title: String) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.createQuickIdeasBox(title).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _createIdeasBoxLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: ${throwable.cause.toString()}")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun updateIdeaBox(id: Int, data: UpdateQuickIdeasBoxData) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.updateQuickIdeaBox(id, data).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _updateIdeasBoxLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: ${throwable.cause.toString()}")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun deleteIdeaBox(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.deleteQuickIdeaBox(id).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _deleteIdeasBoxLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: ${throwable.cause.toString()}")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun getAllIdeasBoxes() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getAllQuickIdeasBoxes().collect {
                it.onSuccess { ideaBoxesList ->
                    _getAllIdeasBoxesLiveData.postValue(ideaBoxesList.data!!)
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