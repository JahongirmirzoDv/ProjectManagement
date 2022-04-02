package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NotePostRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteEditOnesData
import uz.perfectalgorithm.projects.tezkor.domain.home.task.note.NoteRepository
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val repository: NoteRepository) :
    ViewModel() {


    var noteTimeHours: Int = 0
    var noteTimeMinutes: Int = 0
    var noteDate: String = ""

    private val _postNoteLiveData =
        MutableLiveData<NoteData>()
    val postNoteLiveData: LiveData<NoteData> get() = _postNoteLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData


    fun postNote(postRequest: NotePostRequest) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            val data = repository.postNote(postRequest)
            data.collect {
                it.onSuccess { note ->
                    _postNoteLiveData.postValue(note)
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