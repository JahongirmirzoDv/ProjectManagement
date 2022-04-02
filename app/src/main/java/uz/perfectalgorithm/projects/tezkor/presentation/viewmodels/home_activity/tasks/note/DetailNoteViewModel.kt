package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteDeleteOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NotePostRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteEditOnesData
import uz.perfectalgorithm.projects.tezkor.domain.home.task.note.NoteRepository
import javax.inject.Inject

@HiltViewModel
class DetailNoteViewModel @Inject constructor(private val noteRepository: NoteRepository) :
    ViewModel() {


    var noteTimeHours: Int = 0
    var noteTimeMinutes: Int = 0
    var noteDate: String = ""
    var oldTime: String = ""

    private val _getNoteLiveData =
        MutableLiveData<NoteData>()
    val getNoteLiveData: LiveData<NoteData> get() = _getNoteLiveData

    private val _deleteNoteLiveData =
        MutableLiveData<Boolean>()
    val deleteLiveData: LiveData<Boolean> get() = _deleteNoteLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _postNoteLiveData =
        MutableLiveData<NoteData>()
    val postNoteLiveData: LiveData<NoteData> get() = _postNoteLiveData

    private val _editOnesLiveData =
        MutableLiveData<NoteEditOnesData>()
    val editOnesLiveData: LiveData<NoteEditOnesData> get() = _editOnesLiveData


    fun getNote(id: Int) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            val data = noteRepository.getNote(id)
            data.collect {
                it.onSuccess { note ->
                    _getNoteLiveData.postValue(note)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }

        }
    }

    fun deleteNote(id: Int) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            noteRepository.deleteNote(id).collect {
                it.onSuccess { it ->
                    _deleteNoteLiveData.postValue(it)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }

        }
    }

    fun deleteNoteOnes(deleteOnesRequest: NoteDeleteOnesRequest) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            val data = noteRepository.deleteNoteOnes(deleteOnesRequest).collect {
                it.onSuccess { it ->
                    _deleteNoteLiveData.postValue(it)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }


    fun updateNote(id: Int, noteEditRequest: NoteEditRequest) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            val data = noteRepository.updateNote(id, noteEditRequest)
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

    fun updateNoteOnes(noteEditOnesRequest: NoteEditOnesRequest) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            val data = noteRepository.editNoteOnes(noteEditOnesRequest)
            data.collect {
                _progressLiveData.postValue(false)
                it.onSuccess { note ->
                    _editOnesLiveData.postValue(note)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}