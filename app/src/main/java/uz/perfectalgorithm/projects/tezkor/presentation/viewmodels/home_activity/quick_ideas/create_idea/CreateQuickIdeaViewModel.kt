package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.create_idea

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingIdea
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 23.07.2021 17:52
 **/
@HiltViewModel
class CreateQuickIdeaViewModel @Inject constructor(
    private val quickIdeasRepository: QuickIdeasRepository,
) : ViewModel() {

    var title: String? = null
    var description: String? = ""
    var files: ArrayList<File> = ArrayList()
    var ideaBox: Int? = 0
    var ideaBoxesList: ArrayList<IdeasBoxData>? = null

    private val _createQuickIdeaLiveData = MutableLiveData<QuickIdeaData>()
    val createQuickIdeaLiveData: LiveData<QuickIdeaData> get() = _createQuickIdeaLiveData

    private val _getQuickIdeaLiveData = MutableLiveData<RatingIdea>()
    val getQuickIdeaLiveData: LiveData<RatingIdea> get() = _getQuickIdeaLiveData

    private val _updateQuickIdeaLiveData = MutableLiveData<UpdateIdeaItem>()
    val updateQuickLiveData: LiveData<UpdateIdeaItem> get() = _updateQuickIdeaLiveData

    private val _allIdeaBoxLiveData = MutableLiveData<List<IdeasBoxData>>()
    val allIdeaBoxLiveData: LiveData<List<IdeasBoxData>> get() = _allIdeaBoxLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    init {
        if (ideaBoxesList == null) {
            getAllIdeasBoxes()
        }
    }

    fun createQuickIdea(
        title: String,
        description: String?,
        files: List<File>?,
        folder: Int?,
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.createQuickIdea(
                title,
                description,
                files,
                folder,
            ).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _createQuickIdeaLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: $throwable")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun createQuickIdeaWithinBox(
        title: String,
        description: String?,
        files: List<File>?,
        to_idea_box: Boolean
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.createQuickIdeaWithinBox(
                title,
                description,
                files,
                to_idea_box,
            ).collect {
                it.onSuccess { userData ->
                    Log.d("TTTT", "onSuccess : $it")

                    _createQuickIdeaLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("TTTT", "onFailure: $throwable")
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    private fun getAllIdeasBoxes() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getAllQuickIdeasBoxes().collect {
                it.onSuccess { userData ->
                    _allIdeaBoxLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun getQuickIdea(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getQuickIdea(id).collect {
                it.onSuccess { userData ->
                    _getQuickIdeaLiveData.postValue(userData.data!!)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun updateQuickIdea(
        id: Int,
        title: String,
        description: String,
        files: List<File>?,
        folder: Int,
        to_idea_box: Boolean
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.updateQuickIdea(
                id,
                title,
                description,
                files,
                folder,
                to_idea_box
            ).collect {
                it.onSuccess { userData ->
                    _updateQuickIdeaLiveData.postValue(userData.data!!)
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