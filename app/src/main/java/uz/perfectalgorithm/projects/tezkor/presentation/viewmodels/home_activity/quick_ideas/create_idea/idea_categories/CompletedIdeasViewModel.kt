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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.FinishedIdeaItem
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepository
import javax.inject.Inject


@HiltViewModel
class CompletedIdeasViewModel @Inject constructor(
    private val quickIdeasRepository: QuickIdeasRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _getAllRatingIdeas = MutableLiveData<List<FinishedIdeaItem>>()
    val getAllRatingIdeas: LiveData<List<FinishedIdeaItem>> get() = _getAllRatingIdeas


/*    init {
        if (storage.isGeneralIdeaBox) {
            Log.d("SSSS", "CompletedIdeasViewModel IF piece")
            getGeneralFinishedIdeas()
        } else {
            Log.d("SSSS", "CompletedIdeasViewModel ELSE piece")
            getRatingIdeas(storage.ideaBoxId)

        }
    }*/

/*    fun getRatingIdeas(id: Int) {
        if (isConnected()) {
            _progressLiveData.value = true
            viewModelScope.launch(Dispatchers.IO) {
                quickIdeasRepository.getQuickIdeaByFinished(id).collect {
                    it.onSuccess { ideaBoxesList ->
                        _getAllRatingIdeas.postValue(ideaBoxesList)
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

    fun getGeneralFinishedIdeas() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getGeneralQuickIdeaByFinished().collect {
                it.onSuccess { ideaBoxesList ->
                    _getAllRatingIdeas.postValue(ideaBoxesList)
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