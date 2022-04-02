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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.RatingIdeaData
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_idea.QuickIdeasRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 27.07.2021 18:58
 **/
@HiltViewModel
class RatingIdeasViewModel @Inject constructor(
    private val quickIdeasRepository: QuickIdeasRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _getAllRatingIdeas = MutableLiveData<List<RatingIdeaData>>()
    val getAllRatingIdeas: LiveData<List<RatingIdeaData>> get() = _getAllRatingIdeas


/*    init {
        if (storage.isGeneralIdeaBox) {
            Log.d("SSSS", "RatingIdeasViewModel IF piece")
            getGeneralRatingIdeas()
        } else {
            Log.d("SSSS", "RatingIdeasViewModel ELSE piece")

            getRatingIdeas(storage.ideaBoxId)
        }
    }*/

    fun getRatingIdeas(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getQuickIdeaByRating(id).collect {
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

    fun getGeneralRatingIdeas() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            quickIdeasRepository.getGeneralQuickIdeas().collect {
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