package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.quick_ideas.create_idea.idea_categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.QuickIdeaData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.RatingIdeaData
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 27.07.2021 18:58
 **/
@HiltViewModel
class UndoneIdeasViewModel @Inject constructor(
    private val quickIdeasRepository: QuickIdeasRepository,
    private val storage: LocalStorage
) : ViewModel() {


    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _getAllUndoneIdeas = MutableLiveData<List<RatingIdeaData>>()
    val getAllUndoneIdeas: LiveData<List<RatingIdeaData>> get() = _getAllUndoneIdeas

    private val _deleteIdeaLiveData = MutableLiveData<QuickIdeaData>()
    val deleteIdeaLiveData: LiveData<QuickIdeaData> get() = _deleteIdeaLiveData


/*    fun getUndoneIdeas(id: Int) {
        if (isConnected()) {
            _progressLiveData.value = true
            viewModelScope.launch(Dispatchers.IO) {
                quickIdeasRepository.getQuickIdeaByRating(id).collect {
                    it.onSuccess { ideaBoxesList ->
                        _getAllUndoneIdeas.postValue(ideaBoxesList)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable.message)
                        _progressLiveData.postValue(false)
                    }

                }
            }
        }
    }*/

    fun getGeneralUndoneIdeas() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getGeneralQuickIdeas().collect {
                it.onSuccess { ideaBoxesList ->
                    _getAllUndoneIdeas.postValue(ideaBoxesList)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

/*    fun deleteIdea(id: Int) {
        if (isConnected()) {
            _progressLiveData.value = true
            viewModelScope.launch(Dispatchers.IO) {
                quickIdeasRepository.deleteQuickIdea(id).collect {
                    it.onSuccess { ideaBoxesList ->
                        _deleteIdeaLiveData.postValue(ideaBoxesList.data!!)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable.message)
                        _progressLiveData.postValue(false)
                    }

                }
            }
        }
    }*/

}